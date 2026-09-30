package com.tiffino.tiffino.service;

import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.dto.*;
import com.tiffino.tiffino.entity.*;
import com.tiffino.tiffino.entity.response.CartUpdateResponse;
import com.tiffino.tiffino.exception.CustomException;
import com.tiffino.tiffino.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final UserRepo userRepo;
    private final CloudKitchenRepo cloudKitchenRepo;
    private final CloudKitchenMealRepo cloudKitchenMealRepo;
    private final MealRepo mealRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final JwtUtil jwtUtil;




    private static final double SUBSCRIPTION_DISCOUNT_PERCENT = 20.0;
    private static final double OFFER_DISCOUNT_PERCENT = 15.0;
    private static final double ALLERGY_COST = 10.0;

    // -------------------- HELPER METHOD --------------------
    private User getUserFromToken(String token) throws CustomException {
        if (token == null || token.isEmpty()) throw new CustomException("Token is required");
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.extractUsername(jwt);
        if (email == null || email.isEmpty()) throw new CustomException("Invalid token");
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private boolean isSecondWednesday() {
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        while (firstOfMonth.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
            firstOfMonth = firstOfMonth.plusDays(1);
        }
        LocalDate secondWednesday = firstOfMonth.plusDays(7);
        return today.equals(secondWednesday);
    }

    // -------------------- ADD TO CART --------------------
//    @Transactional
//    public void addToCart(String token, AddToCartRequest request) throws Exception {
//        User user = getUserFromToken(token);
//
//        if (request.getCloudKitchenId() == null || request.getCloudKitchenId().isEmpty())
//            throw new CustomException("cloudKitchenId is required");
//        if (request.getMealIds() == null || request.getMealIds().isEmpty())
//            throw new CustomException("mealIds are required");
//
//        CloudKitchen ck = cloudKitchenRepo.findByCloudKitchenIdAndIsDeletedFalse(request.getCloudKitchenId())
//                .orElseThrow(() -> new CustomException("Cloud kitchen not found or deleted: " + request.getCloudKitchenId()));
//
//        Cart cart = cartRepo.findByUser(user).orElse(null);
//        if (cart == null) {
//            cart = Cart.builder()
//                    .user(user)
//                    .cloudKitchenId(ck.getCloudKitchenId())
//                    .cloudKitchenName(ck.getCity() + "-" + ck.getDivision())
//                    .items(new ArrayList<>())
//                    .allergies(new ArrayList<>())
//                    .build();
//            cart = cartRepo.save(cart);
//        } else if (!cart.getCloudKitchenId().equals(ck.getCloudKitchenId())) {
//            throw new CustomException("You can only add meals from one cloud kitchen at a time");
//        }
//
//        for (Long mealId : request.getMealIds()) {
//            Meal meal = mealRepo.findById(mealId)
//                    .orElseThrow(() -> new CustomException("Meal not found: " + mealId));
//
//            CloudKitchenMeal ckMeal = cloudKitchenMealRepo.findByCloudKitchenAndMeal(ck, meal)
//                    .orElseThrow(() -> new CustomException("Meal " + mealId + " is not available at cloud kitchen " + ck.getCloudKitchenId()));
//
//            if (!ckMeal.isAvailable())
//                throw new CustomException("Meal " + mealId + " is not available at cloud kitchen " + ck.getCloudKitchenId());
//
//            Optional<CartItem> existing = cart.getItems().stream()
//                    .filter(ci -> Objects.equals(ci.getMealId(), mealId))
//                    .findFirst();
//
//            if (existing.isPresent()) {
//                CartItem ci = existing.get();
//                ci.setQuantity(ci.getQuantity() + 1);
//                cartItemRepo.save(ci);
//            } else {
//                CartItem ci = CartItem.builder()
//                        .cart(cart)
//                        .mealId(meal.getMealId())
//                        .mealName(meal.getName())
//                        .mealPhoto(meal.getPhotos())
//                        .mealBasePrice(meal.getPrice())
//                        .quantity(1)
//                        .cloudKitchen(ck)
//                        .build();
//                cart.getItems().add(ci);
//                cartItemRepo.save(ci);
//            }
//        }
//
//        cartRepo.save(cart);
//    }

    @Transactional
    public void addToCart(String token, AddToCartRequest request) throws Exception {
        User user = getUserFromToken(token);

        if (request.getCloudKitchenId() == null || request.getCloudKitchenId().isEmpty())
            throw new CustomException("cloudKitchenId is required");
        if (request.getMealIds() == null || request.getMealIds().isEmpty())
            throw new CustomException("mealIds are required");

        CloudKitchen ck = cloudKitchenRepo.findByCloudKitchenIdAndIsDeletedFalse(request.getCloudKitchenId())
                .orElseThrow(() -> new CustomException("Cloud kitchen not found or deleted: " + request.getCloudKitchenId()));

        // ✅ Check if user has an active subscription
        boolean hasActiveSubscription = subscriptionRepo
                .findFirstByUserAndEndDateAfter(user, LocalDateTime.now())
                .isPresent();

        Cart cart = cartRepo.findByUser(user).orElse(null);
        if (cart == null) {
            cart = Cart.builder()
                    .user(user)
                    .cloudKitchenId(ck.getCloudKitchenId())
                    .cloudKitchenName(ck.getCity() + "-" + ck.getDivision())
                    .items(new ArrayList<>())
                    .allergies(new ArrayList<>())
                    .build();
            cart = cartRepo.save(cart);
        } else if (!cart.getCloudKitchenId().equals(ck.getCloudKitchenId())) {
            throw new CustomException("You can only add meals from one cloud kitchen at a time");
        }

        for (Long mealId : request.getMealIds()) {
            Meal meal = mealRepo.findById(mealId)
                    .orElseThrow(() -> new CustomException("Meal not found: " + mealId));

            CloudKitchenMeal ckMeal = cloudKitchenMealRepo.findByCloudKitchenAndMeal(ck, meal)
                    .orElseThrow(() -> new CustomException("Meal " + mealId + " is not available at cloud kitchen " + ck.getCloudKitchenId()));

            if (!ckMeal.isAvailable())
                throw new CustomException("Meal " + mealId + " is not available at cloud kitchen " + ck.getCloudKitchenId());

            Optional<CartItem> existing = cart.getItems().stream()
                    .filter(ci -> Objects.equals(ci.getMealId(), mealId))
                    .findFirst();

            double mealBasePrice = hasActiveSubscription ? 0.0 : meal.getPrice(); // ✅ Dynamic price

            if (existing.isPresent()) {
                CartItem ci = existing.get();
                ci.setQuantity(ci.getQuantity() + 1);
                ci.setMealBasePrice(mealBasePrice); // ✅ Update price if subscription toggled later
                cartItemRepo.save(ci);
            } else {
                CartItem ci = CartItem.builder()
                        .cart(cart)
                        .mealId(meal.getMealId())
                        .mealName(meal.getName())
                        .mealPhoto(meal.getPhotos())
                        .mealBasePrice(mealBasePrice)
                        .quantity(1)
                        .cloudKitchen(ck)
                        .build();
                cart.getItems().add(ci);
                cartItemRepo.save(ci);
            }
        }

        cartRepo.save(cart);
    }


    // -------------------- VIEW CART --------------------
    /**  @Transactional(readOnly = true)
    public CartResponse viewCart(String token) throws Exception {
    User user = getUserFromToken(token);

    Cart cart = cartRepo.findByUser(user).orElse(null);
    if (cart == null || cart.getItems().isEmpty()) {
    return new CartResponse(null, null, Collections.emptyList(), Collections.emptyList(), 0.0);
    }

    boolean hasActiveSubscription = subscriptionRepo
    .findFirstByUserAndEndDateAfter(user, LocalDateTime.now())
    .isPresent();
    boolean isOfferDay = isSecondWednesday();

    List<CartItemResponse> items = cart.getItems().stream().map(ci -> {
    double basePrice = ci.getMealBasePrice() != null ? ci.getMealBasePrice() : 0.0;
    double discountedPrice = basePrice;

    if (hasActiveSubscription)
    discountedPrice = round(basePrice * (1 - SUBSCRIPTION_DISCOUNT_PERCENT / 100));
    else if (!hasActiveSubscription && isOfferDay)
    discountedPrice = round(basePrice * (1 - OFFER_DISCOUNT_PERCENT / 100));

    double lineTotal = round(discountedPrice * (ci.getQuantity() != null ? ci.getQuantity() : 1));

    return new CartItemResponse(
    ci.getMealId(),
    ci.getMealName(),
    ci.getMealPhoto(),
    ci.getQuantity(),
    round(basePrice),
    discountedPrice,
    lineTotal
    );
    }).collect(Collectors.toList());

    double allergyCost = cart.getAllergies() != null ? cart.getAllergies().size() * 10.0 : 0.0;
    double totalAmount = round(items.stream().mapToDouble(CartItemResponse::getLineTotal).sum() + allergyCost);

    return new CartResponse(
    cart.getCloudKitchenId(),
    cart.getCloudKitchenName(),
    items,
    cart.getAllergies(),
    totalAmount
    );
    }**/
    // -------------------- VIEW CART --------------------
//    @Transactional(readOnly = true)
//    public CartResponse viewCart(String token) throws Exception {
//        User user = getUserFromToken(token);
//        Cart cart = cartRepo.findByUser(user).orElse(null);
//        if (cart == null || cart.getItems().isEmpty()) {
//            return new CartResponse(null, null, Collections.emptyList(), Collections.emptyList(), 0.0);
//        }
//
//        boolean hasActiveSubscription = subscriptionRepo
//                .findFirstByUserAndEndDateAfter(user, LocalDateTime.now())
//                .isPresent();
//        boolean isOfferDay = isSecondWednesday();
//
//        List<CartItemResponse> items = cart.getItems().stream().map(ci -> {
//            double basePrice = ci.getMealBasePrice() != null ? ci.getMealBasePrice() : 0.0;
//            double discountedPrice = basePrice;
//
//            if (hasActiveSubscription)
//                discountedPrice = round(basePrice * (1 - SUBSCRIPTION_DISCOUNT_PERCENT / 100));
//            else if (!hasActiveSubscription && isOfferDay)
//                discountedPrice = round(basePrice * (1 - OFFER_DISCOUNT_PERCENT / 100));
//
//            double lineTotal = round(discountedPrice * (ci.getQuantity() != null ? ci.getQuantity() : 1));
//
//            return new CartItemResponse(
//                    ci.getMealId(),
//                    ci.getMealName(),
//                    ci.getMealPhoto(),
//                    ci.getQuantity(),
//                    round(basePrice),
//                    discountedPrice,
//                    lineTotal
//            );
//        }).collect(Collectors.toList());
//
//        double allergyCost = cart.getAllergies() != null ? cart.getAllergies().size() * ALLERGY_COST : 0.0;
//        double totalAmount = round(items.stream().mapToDouble(CartItemResponse::getLineTotal).sum() + allergyCost);
//
//        return new CartResponse(
//                cart.getCloudKitchenId(),
//                cart.getCloudKitchenName(),
//                items,
//                cart.getAllergies(),
//                totalAmount
//        );
//    }


    @Transactional(readOnly = true)
    public CartResponse viewCart(String token) throws Exception {
        User user = getUserFromToken(token);
        Cart cart = cartRepo.findByUser(user).orElse(null);
        if (cart == null || cart.getItems().isEmpty()) {
            // Empty cart → still send hasSubscription info
            boolean hasActiveSubscription = subscriptionRepo
                    .findFirstByUserAndEndDateAfter(user, LocalDateTime.now())
                    .isPresent();

            return new CartResponse(null, null, Collections.emptyList(), Collections.emptyList(), 0.0, hasActiveSubscription);
        }

        boolean hasActiveSubscription = subscriptionRepo
                .findFirstByUserAndEndDateAfter(user, LocalDateTime.now())
                .isPresent();

        boolean isOfferDay = isSecondWednesday();

        List<CartItemResponse> items = cart.getItems().stream().map(ci -> {
            double basePrice = ci.getMealBasePrice() != null ? ci.getMealBasePrice() : 0.0;
            double discountedPrice;

            if (hasActiveSubscription) {
                discountedPrice = 0.0; // ✅ Price becomes 0 for active subscription
            } else if (isOfferDay) {
                discountedPrice = round(basePrice * (1 - OFFER_DISCOUNT_PERCENT / 100));
            } else {
                discountedPrice = basePrice;
            }

            double lineTotal = round(discountedPrice * (ci.getQuantity() != null ? ci.getQuantity() : 1));

            return new CartItemResponse(
                    ci.getMealId(),
                    ci.getMealName(),
                    ci.getMealPhoto(),
                    ci.getQuantity(),
                    round(basePrice),
                    discountedPrice,
                    lineTotal
            );
        }).collect(Collectors.toList());

        double allergyCost = cart.getAllergies() != null ? cart.getAllergies().size() * ALLERGY_COST : 0.0;
        double totalAmount = round(items.stream().mapToDouble(CartItemResponse::getLineTotal).sum() + allergyCost);

        // ✅ Include hasSubscription in the response
        return new CartResponse(
                cart.getCloudKitchenId(),
                cart.getCloudKitchenName(),
                items,
                cart.getAllergies(),
                totalAmount,
                hasActiveSubscription
        );
    }





    // -------------------- UPDATE CART QUANTITY --------------------
    @Transactional
    public CartResponse updateCartQty(String token, UpdateCartQtyRequest request) throws Exception {
        User user = getUserFromToken(token);

        Cart cart = cartRepo.findByUser(user).orElseThrow(() -> new CustomException("Cart is empty"));

        for (CartItemQtyUpdate u : request.getItems()) {
            Long mealId = u.getMealId();
            Integer qty = u.getQty() != null ? u.getQty() : 0;

            Optional<CartItem> ciOpt = cart.getItems().stream()
                    .filter(ci -> ci.getMealId().equals(mealId))
                    .findFirst();

            if (ciOpt.isEmpty()) continue;

            CartItem ci = ciOpt.get();
            if (qty <= 0) {
                cart.getItems().remove(ci);
                cartItemRepo.delete(ci);
            } else {
                ci.setQuantity(qty);
                cartItemRepo.save(ci);
            }
        }

        cartRepo.save(cart);
        return viewCart(token);
    }

    // -------------------- REMOVE FROM CART --------------------
    @Transactional
    public CartResponse removeFromCart(String token, RemoveFromCartRequest request) throws Exception {
        User user = getUserFromToken(token);

        Cart cart = cartRepo.findByUser(user).orElseThrow(() -> new CustomException("Cart is empty"));

        for (Long mealId : request.getMealIds()) {
            cart.getItems().stream()
                    .filter(ci -> ci.getMealId().equals(mealId))
                    .findFirst()
                    .ifPresent(ci -> {
                        cart.getItems().remove(ci);
                        cartItemRepo.delete(ci);
                    });
        }

        cartRepo.save(cart);
        return viewCart(token);
    }

    // -------------------- CLEAR CART --------------------
    @Transactional
    public void clearCart(String token) throws Exception {
        User user = getUserFromToken(token);
        cartRepo.findByUser(user).ifPresent(cartRepo::delete);
    }

    // -------------------- ADD ALLERGIES --------------------
    /**  @Transactional
    public CartResponse addAllergies(String token, List<String> newAllergies) throws Exception {
    User user = getUserFromToken(token);

    Cart cart = cartRepo.findByUser(user).orElseThrow(() -> new CustomException("Cart is empty"));

    if (newAllergies != null) {
    if (cart.getAllergies() == null) cart.setAllergies(new ArrayList<>());
    cart.getAllergies().addAll(newAllergies);
    }

    cartRepo.save(cart);
    return viewCart(token);
    }**/

    //AddOrRemoveAllergies
    // -------------------- UPDATE ALLERGIES (ADD + REMOVE) --------------------
    /**  @Transactional
    public CartResponse updateAllergies(String token, AllergiesUpdateRequest request) throws Exception {
    User user = getUserFromToken(token);

    Cart cart = cartRepo.findByUser(user)
    .orElseThrow(() -> new CustomException("Cart is empty"));

    if (cart.getAllergies() == null) cart.setAllergies(new ArrayList<>());

    // ✅ Add allergies
    if (request.getAdd() != null) {
    for (String allergy : request.getAdd()) {
    if (!cart.getAllergies().contains(allergy)) {
    cart.getAllergies().add(allergy);
    }
    }
    }

    // ✅ Remove allergies
    if (request.getRemove() != null) {
    cart.getAllergies().removeAll(request.getRemove());
    }

    cartRepo.save(cart);

    return viewCart(token);
    }**/
    @Transactional
    public CartUpdateResponse updateAllergies(String token, AllergiesUpdateRequest request) throws Exception {
        User user = getUserFromToken(token);
        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new CustomException("Cart is empty"));

        boolean hasActiveSubscription = subscriptionRepo
                .findTopByUserOrderByEndDateDesc(user)
                .map(sub -> sub.isActive() && sub.getEndDate().isAfter(LocalDateTime.now()))
                .orElse(false);

        String message = "";

        if (request.getAdd() != null && !request.getAdd().isEmpty()) {
            if (hasActiveSubscription) {
                throw new CustomException("You cannot add allergies while having an active subscription!");
            } else {
                if (cart.getAllergies() == null) cart.setAllergies(new ArrayList<>());
                for (String allergy : request.getAdd()) {
                    if (!cart.getAllergies().contains(allergy)) {
                        cart.getAllergies().add(allergy);
                        cart.setTotalAmount(cart.getTotalAmount() + ALLERGY_COST);
                    }
                }
                message = "Allergies added successfully";
            }
        }

        if (request.getRemove() != null && !request.getRemove().isEmpty()) {
            if (cart.getAllergies() != null) {
                for (String allergy : request.getRemove()) {
                    if (cart.getAllergies().remove(allergy)) {
                        cart.setTotalAmount(cart.getTotalAmount() - ALLERGY_COST);
                    }
                }
            }
            message = "Allergies removed successfully";
        }

        cartRepo.save(cart);
        CartResponse updatedCart = viewCart(token);

        return new CartUpdateResponse(updatedCart, message);
    }

}
