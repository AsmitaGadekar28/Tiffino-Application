/**package com.tiffino.tiffino.service;

import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.entity.*;
import com.tiffino.tiffino.entity.request.OrderRequest;
import com.tiffino.tiffino.entity.response.*;
import com.tiffino.tiffino.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepo userRepo;
    private final ManagerRepo managerRepo;
    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final CloudKitchenMealRepo cloudKitchenMealRepo;
    private final JwtUtil jwtUtil;

    private static final double SUBSCRIPTION_DISCOUNT_PERCENT = 20.0;
    private static final double OFFER_DISCOUNT_PERCENT = 15.0;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ================= CREATE ORDER =================
    public CreateOrderResponse createOrder(String token, OrderRequest request) {
        User user = extractUserFromToken(token);

        boolean hasActiveSubscription = subscriptionRepo
                .findFirstByUserAndEndDateAfter(user, LocalDateTime.now())
                .isPresent();

        LocalDate today = LocalDate.now();
        boolean isOfferDay = isSecondWednesday(today);

        List<Cart> carts = cartRepo.findAllByUser(user);
        if (carts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty!");
        }

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        CloudKitchen orderKitchen = null;

        for (Cart cart : carts) {
            for (CartItem item : cart.getItems()) {
                CloudKitchenMeal ckm = cloudKitchenMealRepo
                        .findFirstByMeal_MealId(item.getMealId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "CloudKitchen mapping not found for meal ID: " + item.getMealId()
                        ));

                if (orderKitchen == null) {
                    orderKitchen = ckm.getCloudKitchen();
                } else if (!orderKitchen.equals(ckm.getCloudKitchen())) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "All meals in an order must belong to the same CloudKitchen!"
                    );
                }

                double basePrice = item.getMealBasePrice() * item.getQuantity();
                double discountedPrice = hasActiveSubscription
                        ? basePrice * (1 - SUBSCRIPTION_DISCOUNT_PERCENT / 100)
                        : isOfferDay ? basePrice * (1 - OFFER_DISCOUNT_PERCENT / 100)
                        : basePrice;

                totalAmount += discountedPrice;

                OrderItem orderItem = OrderItem.builder()
                        .mealId(item.getMealId())
                        .mealName(item.getMealName())
                        .mealPhoto(item.getMealPhoto())
                        .mealPrice(discountedPrice)
                        .quantity(item.getQuantity())
                        .build();

                orderItems.add(orderItem);
            }
        }

        if (orderKitchen == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No valid CloudKitchen found for order items!");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCloudKitchen(orderKitchen);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setAddress(request.getAddress());
        order.setCity(request.getCity());
        order.setState(request.getState());
        order.setPinCode(request.getPinCode());
        order.setItems(orderItems);

        Order savedOrder = orderRepo.save(order);
        cartRepo.deleteAll(carts);

        return new CreateOrderResponse(
                "Order created successfully!",
                savedOrder.getOrderId(),
                savedOrder.getTotalAmount(),
                savedOrder.getOrderTime(),
                savedOrder.getOrderTime().toLocalDate().format(dateFormatter)
        );
    }

    // ================= USER ORDERS =================
    public List<UserOrderResponse> getAllOrdersByUser(String token) {
        User user = extractUserFromToken(token);

        return orderRepo.findAllByUserOrderByOrderTimeDesc(user)
                .stream()
                .map(order -> new UserOrderResponse(
                        order.getOrderId(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getOrderTime(),
                        order.getOrderTime().toLocalDate().format(dateFormatter),
                        order.getAddress(),
                        order.getCity(),
                        order.getState(),
                        order.getPinCode(),
                        order.getItems().stream().map(oi ->
                                new OrderItemResponse(
                                        oi.getMealName(),
                                        oi.getMealPhoto(),
                                        oi.getQuantity(),
                                        oi.getMealPrice(),
                                        oi.getMealPrice()
                                )
                        ).toList()
                ))
                .toList();
    }

    // ================= MANAGER ORDERS =================
    public List<ManagerOrderResponse> getAllOrdersForManager(String token) {
        Manager manager = extractManagerFromToken(token);
        CloudKitchen managerKitchen = manager.getCloudKitchen();

        if(managerKitchen == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manager has no assigned CloudKitchen!");
        }

        List<Order> orders = orderRepo.findAllByCloudKitchenOrderByOrderTimeDesc(managerKitchen);

        return orders.stream()
                .map(order -> new ManagerOrderResponse(
                        order.getOrderId(),
                        order.getUser().getUserName(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getOrderTime(),
                        order.getOrderTime().toLocalDate().format(dateFormatter),
                        order.getAddress(),
                        order.getCity(),
                        order.getState(),
                        order.getPinCode()
                ))
                .toList();
    }

    // ================= HELPER METHODS =================
    private User extractUserFromToken(String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.extractUsername(jwt);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != Role.USER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied! Only USER role allowed");
        }
        return user;
    }

    private Manager extractManagerFromToken(String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.extractUsername(jwt);

        return managerRepo.findByManagerEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found"));
    }

    private boolean isSecondWednesday(LocalDate date) {
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        LocalDate firstWednesday = firstOfMonth;
        while (firstWednesday.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
            firstWednesday = firstWednesday.plusDays(1);
        }
        LocalDate secondWednesday = firstWednesday.plusDays(7);
        return date.equals(secondWednesday);
    }
}
**/

package com.tiffino.tiffino.service;

import ch.qos.logback.classic.boolex.MarkerList;
import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.entity.*;
import com.tiffino.tiffino.entity.request.OrderRequest;
import com.tiffino.tiffino.entity.response.*;
import com.tiffino.tiffino.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepo userRepo;
    private final ManagerRepo managerRepo;
    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final CloudKitchenMealRepo cloudKitchenMealRepo;
    private final JwtUtil jwtUtil;

    private static final double SUBSCRIPTION_DISCOUNT_PERCENT = 20.0;
    private static final double OFFER_DISCOUNT_PERCENT = 15.0;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Set<Long> validOrders = ConcurrentHashMap.newKeySet();


    // ================= CREATE ORDER =================
    public CreateOrderResponse createOrder(String token, OrderRequest request) {
        User user = extractUserFromToken(token);

        List<Cart> carts = cartRepo.findAllByUser(user);
        if (carts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty!");
        }

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        CloudKitchen orderKitchen = null;

        boolean hasActiveSubscription = subscriptionRepo
                .findFirstByUserAndEndDateAfter(user, LocalDateTime.now())
                .isPresent();

        LocalDate today = LocalDate.now();
        boolean isOfferDay = isSecondWednesday(today);

        List<String> orderAllergies = new ArrayList<>(); // ✅ For copying allergies

        for (Cart cart : carts) {
            // Copy allergies from cart to order (create new list to avoid shared reference)
            if (cart.getAllergies() != null) {
                orderAllergies.addAll(cart.getAllergies());
            }

            for (CartItem item : cart.getItems()) {
                CloudKitchen currentKitchen = item.getCloudKitchen();

                if (orderKitchen == null) {
                    orderKitchen = currentKitchen;
                } else if (!Objects.equals(orderKitchen.getCloudKitchenId(), currentKitchen.getCloudKitchenId())) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "All meals in an order must belong to the same CloudKitchen!"
                    );
                }

                double basePrice = item.getMealBasePrice() * item.getQuantity();
                double discountedPrice;
                if (hasActiveSubscription) {
                    discountedPrice = basePrice * (1 - SUBSCRIPTION_DISCOUNT_PERCENT / 100);
                } else if (isOfferDay) {
                    discountedPrice = basePrice * (1 - OFFER_DISCOUNT_PERCENT / 100);
                } else {
                    discountedPrice = basePrice;
                }

                totalAmount += discountedPrice;

                OrderItem orderItem = OrderItem.builder()
                        .mealId(item.getMealId())
                        .mealName(item.getMealName())
                        .mealPhoto(item.getMealPhoto())
                        .mealPrice(discountedPrice)
                        .quantity(item.getQuantity())
                        .build();

                orderItems.add(orderItem);
            }
        }

        // ✅ Add allergy cost
        double allergyCost = orderAllergies.size() * 10.0;
        totalAmount += allergyCost;

        if (orderKitchen == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No valid CloudKitchen found for order items!");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCloudKitchen(orderKitchen);
        order.setItems(orderItems);
        order.setAllergies(orderAllergies); // ✅ Copy set
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setAddress(request.getAddress());
        order.setCity(request.getCity());
        order.setState(request.getState());
        order.setPinCode(request.getPinCode());

        Order savedOrder = orderRepo.save(order);

        // Clear cart after order
        cartRepo.deleteAll(carts);

        return new CreateOrderResponse(
                "Order created successfully!",
                savedOrder.getOrderId(),
                savedOrder.getTotalAmount(),
                savedOrder.getOrderTime(),
                savedOrder.getOrderTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
    }

    // ================= USER ORDERS =================
    public List<UserOrderResponse> getAllOrdersByUser(String token) {
        User user = extractUserFromToken(token);

        return orderRepo.findAllByUserOrderByOrderTimeDesc(user)
                .stream()
                .map(order -> new UserOrderResponse(
                        order.getOrderId(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getOrderTime(),
                        order.getOrderTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        order.getAddress(),
                        order.getCity(),
                        order.getState(),
                        order.getPinCode(),
                        order.getItems().stream()
                                .map(oi -> new OrderItemResponse(
                                        oi.getMealName(),
                                        oi.getMealPhoto(),
                                        oi.getQuantity(),
                                        oi.getMealPrice(),
                                        oi.getMealPrice()
                                )).toList(),
                        order.getAllergies() // show allergies
                ))
                .toList();
    }

    // ================= MANAGER ORDERS =================
    /** public List<ManagerOrderResponse> getAllOrdersForManager(String token) {
     Manager manager = extractManagerFromToken(token);
     CloudKitchen managerKitchen = manager.getCloudKitchen();

     if (managerKitchen == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manager has no assigned CloudKitchen!");

     List<Order> orders = orderRepo.findAllByCloudKitchenOrderByOrderTimeDesc(managerKitchen);

     return orders.stream()
     .map(order -> {
     double allergyCost = order.getAllergies() != null ? order.getAllergies().size() * 10.0 : 0.0;
     double totalAmountWithAllergies = order.getTotalAmount() + allergyCost;

     return new ManagerOrderResponse(
     order.getOrderId(),
     order.getUser().getUserName(),
     totalAmountWithAllergies,
     order.getStatus(),
     order.getOrderTime(),
     order.getOrderTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
     order.getAddress(),
     order.getCity(),
     order.getState(),
     order.getPinCode(),
     order.getAllergies() // show allergies
     );
     })
     .toList();
     }**/

    public List<ManagerOrderResponse> getAllOrdersForManager(String token) {
        Manager manager = extractManagerFromToken(token);
        CloudKitchen managerKitchen = manager.getCloudKitchen();

        if (managerKitchen == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manager has no assigned CloudKitchen!");

        return orderRepo.findAllByCloudKitchenOrderByOrderTimeDesc(managerKitchen)
                .stream()
                .map(order -> new ManagerOrderResponse(
                        order.getOrderId(),
                        order.getUser().getUserName(),
                        order.getTotalAmount(),  // ✅ Directly user ka total amount use karo (allergies included)
                        order.getStatus(),
                        order.getOrderTime(),
                        order.getOrderTime().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        order.getAddress(),
                        order.getCity(),
                        order.getState(),
                        order.getPinCode(),
                        order.getItems().stream()
                                .map(oi -> new OrderItemResponse(
                                        oi.getMealName(),
                                        oi.getMealPhoto(),
                                        oi.getQuantity(),
                                        oi.getMealPrice(),
                                        oi.getMealPrice()
                                )).toList(),
                        order.getAllergies()  // ✅ Only displaying allergies, no extra charge calculation
                ))
                .toList();
    }


    // ================= HELPER METHODS =================
    private User extractUserFromToken(String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.extractUsername(jwt);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != Role.USER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied! Only USER role allowed");
        }
        return user;
    }

    private Manager extractManagerFromToken(String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.extractUsername(jwt);

        return managerRepo.findByManagerEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found"));
    }

    private boolean isSecondWednesday(LocalDate date) {
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        while (firstOfMonth.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
            firstOfMonth = firstOfMonth.plusDays(1);
        }
        LocalDate secondWednesday = firstOfMonth.plusDays(7);
        return date.equals(secondWednesday);
    }



    // ================= ACCEPT ORDER =================
    public String acceptOrder(String token, Long orderId) {
        Manager manager = extractManagerFromToken(token);
        CloudKitchen kitchen = manager.getCloudKitchen();

        if (kitchen == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manager has no assigned CloudKitchen!");
        }

        Order order = orderRepo.findByOrderIdAndCloudKitchen(orderId, kitchen)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for this kitchen!"));

        if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order can only be accepted if it's currently PENDING!");
        }

        order.setStatus("ACCEPTED");
        orderRepo.save(order);

        return "Order #" + order.getOrderId() + " has been accepted successfully.";
    }


    // ================= PREPARING ORDER =================
    public String preparingOrder(String token, Long orderId) {
        Manager manager = extractManagerFromToken(token);
        CloudKitchen kitchen = manager.getCloudKitchen();

        if (kitchen == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manager has no assigned CloudKitchen!");
        }

        Order order = orderRepo.findByOrderIdAndCloudKitchen(orderId, kitchen)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for this kitchen!"));

        if (!"ACCEPTED".equalsIgnoreCase(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order can only move to PREPARING if it's currently ACCEPTED!");
        }

        order.setStatus("PREPARING");
        orderRepo.save(order);

        return "Order #" + order.getOrderId() + " is now in PREPARING state.";
    }


    // ================= GET ORDER SUMMARY =================
    public java.util.Map<String, Object> getOrderSummary(String token) {
        Manager manager = extractManagerFromToken(token);
        CloudKitchen kitchen = manager.getCloudKitchen();

        if (kitchen == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manager has no assigned CloudKitchen!");
        }

        // Fetch all orders for this CloudKitchen (use existing repo method)
        List<Order> allOrders = orderRepo.findAllByCloudKitchenOrderByOrderTimeDesc(kitchen);

        // Filter by status
        List<Order> pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus().equalsIgnoreCase("PENDING"))
                .toList();

        List<Order> assignedOrders = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus().equalsIgnoreCase("ASSIGNED"))
                .toList();

        List<Order> deliveredOrders = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus().equalsIgnoreCase("DELIVERED"))
                .toList();

        // Calculate total cost (sum of totalAmount)
        double totalOrdersCost = allOrders.stream()
                .mapToDouble(o -> o.getTotalAmount())
                .sum();

        // Build compact order detail DTOs to avoid sending entire JPA entities
        java.util.List<java.util.Map<String, Object>> pendingDetails = toOrderSummaryList(pendingOrders);
        java.util.List<java.util.Map<String, Object>> assignedDetails = toOrderSummaryList(assignedOrders);
        java.util.List<java.util.Map<String, Object>> deliveredDetails = toOrderSummaryList(deliveredOrders);

        java.util.Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("cloudKitchenName", kitchen.getName()); // use getName() from CloudKitchen
        response.put("totalOrdersCount", allOrders.size());
        response.put("totalOrdersCost", totalOrdersCost);

        response.put("pendingOrdersCount", pendingOrders.size());
        response.put("pendingOrdersDetails", pendingDetails);

        response.put("assignedOrdersCount", assignedOrders.size());
        response.put("assignedOrdersDetails", assignedDetails);

        response.put("deliveredOrdersCount", deliveredOrders.size());
        response.put("deliveredOrdersDetails", deliveredDetails);

        return response;
    }

    /**
     * Helper to convert Order list to a compact map for JSON response
     * (orderId, status, totalAmount, orderTime, customerName, customerEmail)
     */
    private java.util.List<java.util.Map<String, Object>> toOrderSummaryList(List<Order> orders) {
        return orders.stream().map(o -> {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("orderId", o.getOrderId());
            m.put("status", o.getStatus());
            m.put("totalAmount", o.getTotalAmount());
            m.put("orderTime", o.getOrderTime());
            if (o.getUser() != null) {
                m.put("customerName", o.getUser().getUserName());
                m.put("customerEmail", o.getUser().getEmail());
            } else {
                m.put("customerName", null);
                m.put("customerEmail", null);
            }

            // Address Info
            m.put("address", o.getAddress());
            m.put("city", o.getCity());
            m.put("state", o.getState());
            m.put("pinCode", o.getPinCode());

            return m;
        }).toList();
    }
   

}