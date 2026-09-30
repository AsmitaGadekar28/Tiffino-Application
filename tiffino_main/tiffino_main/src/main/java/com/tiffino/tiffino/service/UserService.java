package com.tiffino.tiffino.service;

import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.dto.CuisineDTO;
import com.tiffino.tiffino.entity.*;
import com.tiffino.tiffino.entity.request.RegisterRequest;
import com.tiffino.tiffino.entity.request.ReviewRequest;
import com.tiffino.tiffino.entity.request.UpdateUserRequest;
import com.tiffino.tiffino.entity.response.MealByStateResponse;
import com.tiffino.tiffino.entity.response.MealWithCuisineResponse;
import com.tiffino.tiffino.entity.response.TrackOrderResponse;
import com.tiffino.tiffino.exception.CustomException;
import com.tiffino.tiffino.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final CloudKitchenMealRepo cloudKitchenMealRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final JwtUtil jwtUtil;

    private final OrderRepo orderRepo;
    private final ReviewRepo reviewRepo;
    private final CloudKitchenRepo cloudKitchenRepo;
    private final CuisineRepo cuisineRepo;

    @Autowired
    private EmailValidationService emailValidationService;

    private static final double SUBSCRIPTION_DISCOUNT_PERCENT = 20.0;
    private static final double OFFER_DISCOUNT_PERCENT = 15.0;

    //  User Registration
    public String register(RegisterRequest req) {

        // ✅ Email deliverability check
        if (!emailValidationService.isEmailDeliverable(req.getEmail())) {
            throw new CustomException("Invalid Or Not Deliverable email");
        }
        if (userRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already exists!");
        if (userRepo.existsByPhoneNo(req.getPhoneNo()))
            throw new RuntimeException("Phone number already exists!");

        User user = new User();
        user.setUserName(req.getUserName());
        user.setEmail(req.getEmail());
        user.setPhoneNo(req.getPhoneNo());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        userRepo.save(user);
        return "User registered successfully!";
    }

    //  Get all available meals with cuisine + discount logic
//


    public List<MealWithCuisineResponse> getAllAvailableMealsWithCuisine(String token) {

        String jwtToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : token;

        final boolean hasActiveSubscription;
        boolean subscriptionActive = false;

        if (jwtToken != null && !jwtToken.isEmpty()) {
            try {
                String userEmail = jwtUtil.extractUsername(jwtToken);
                if (userEmail != null && !userEmail.isEmpty()) {
                    User user = userRepo.findByEmail(userEmail)
                            .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

                    subscriptionActive = subscriptionRepo
                            .findFirstByUserAndEndDateAfter(user, LocalDateTime.now())
                            .isPresent();
                }
            } catch (Exception ignored) {}
        }

        hasActiveSubscription = subscriptionActive;

        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        LocalDate firstWednesday = firstOfMonth;
        while (firstWednesday.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
            firstWednesday = firstWednesday.plusDays(1);
        }
        LocalDate secondWednesday = firstWednesday.plusDays(7);
        boolean isOfferDay = today.equals(secondWednesday);

        List<CloudKitchenMeal> availableMeals = cloudKitchenMealRepo.findAllAvailableFromActiveKitchens();

        return availableMeals.stream()
                .filter(meal -> meal != null && meal.getMeal() != null && meal.getCloudKitchen() != null)
                .map(meal -> {
                    String cloudKitchenName = meal.getCloudKitchen().getCity() + "_" + meal.getCloudKitchen().getDivision();
                    double basePrice = meal.getMeal().getPrice();
                    double finalPrice;

                    if (hasActiveSubscription) {
                        finalPrice = 0.0; // 💥 price is zero for subscribed users
                    } else if (isOfferDay) {
                        finalPrice = basePrice * (1 - OFFER_DISCOUNT_PERCENT / 100);
                    } else {
                        finalPrice = basePrice;
                    }

                    return new MealWithCuisineResponse(
                            meal.getMeal().getMealId(),
                            meal.getMeal().getName(),
                            basePrice,
                            finalPrice,
                            meal.getMeal().getPhotos(),
                            meal.getMeal().getNutritionalInformation(),
                            meal.getMeal().getDescription(),
                            meal.getMeal().getCuisine() != null ? meal.getMeal().getCuisine().getCuisineId() : null,
                            meal.getMeal().getCuisine() != null ? meal.getMeal().getCuisine().getName() : null,
                            meal.getCloudKitchen().getCloudKitchenId(),
                            cloudKitchenName,
                            meal.getCloudKitchen().getIsOpen()
                    );
                })
                .collect(Collectors.toList());
    }


    //  Update user details
    public User updateUser(String token, UpdateUserRequest request) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.extractUsername(jwt);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (request.getUserName() != null) user.setUserName(request.getUserName());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getPhoneNo() != null) user.setPhoneNo(request.getPhoneNo());
        if (request.getMealPreference() != null) user.setMealPreference(request.getMealPreference());
        if (request.getDietaryNeeds() != null) user.setDietaryNeeds(request.getDietaryNeeds());

        return userRepo.save(user);
    }

    //  Get offer message for second Wednesday
    public String getOfferMessage() {
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);

        // Find first Wednesday
        LocalDate firstWednesday = firstOfMonth;
        while (firstWednesday.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
            firstWednesday = firstWednesday.plusDays(1);
        }

        // Second Wednesday
        LocalDate secondWednesday = firstWednesday.plusDays(7);

        boolean isOfferDay = today.equals(secondWednesday);

        return isOfferDay ? "Today’s Offer: 15% off on all meals!" : "No offer available right now";
    }

    //cancel order
    public String cancelOrder(Long orderId, String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException("You cannot cancel someone else's order!");
        }

        if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
            throw new CustomException("You can cancel the order only if it is in PENDING status");
        }

        order.setCancelled(true);
        order.setStatus("CANCELLED");
        order.setAssignedAt(LocalDateTime.now());
        orderRepo.save(order);

        return "Order Cancelled Successfully!";
    }

    //create review
    public String createReview(ReviewRequest request, String email) {
        // Fetch user
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        //  Fetch order
        Order order = orderRepo.findById(request.getOrderId())
                .orElseThrow(() -> new CustomException("Order not found"));

        //  Check if order belongs to this user
        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException("You cannot review someone else's order!");
        }

        //  Check if order is delivered
        if (!"DELIVERED".equalsIgnoreCase(order.getStatus())) {
            throw new CustomException("You can only review delivered orders");
        }

        //  Ensure review text is present
        String reviewText = request.getCloudKitchenReview();
        if (reviewText == null || reviewText.trim().isEmpty()) {
            throw new CustomException("Review cannot be empty");
        }

        // Ensure rating is valid (1-5)
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new CustomException("Rating must be between 1 and 5");
        }

        //  Fetch CloudKitchen from repository to avoid detached entity issues
        CloudKitchen cloudKitchen = cloudKitchenRepo.findById(order.getCloudKitchen().getCloudKitchenId())
                .orElseThrow(() -> new CustomException("CloudKitchen not found for this order"));

        //  Create and save review
        Review review = new Review();
        review.setCloudKitchenReview(reviewText.trim());
        review.setRating(request.getRating());
        review.setUser(user);
        review.setOrder(order);
        review.setCloudKitchen(cloudKitchen);

        reviewRepo.save(review);

        return "Review Submitted Successfully!";
    }


    //track order
    public TrackOrderResponse trackOrder(Long orderId, String email) {
        //  1. Get user by email (from token)
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        // 2. Get order
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        // 3. Ensure the order belongs to this user
        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException("You cannot view someone else's order!");
        }

        //  4. Get delivery person info
        DeliveryPerson dp = order.getDeliveryPerson();

        //  5. Return response
        return TrackOrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getStatus())
                .deliveryPersonName(dp != null ? dp.getName() : null)
                .deliveryPersonPhoneNo(dp != null ? dp.getPhoneNo() : null)
                .assignedAt(order.getAssignedAt() != null ? order.getAssignedAt().toLocalTime() : null)
                .pickUpAt(order.getPickUpAt() != null ? order.getPickUpAt().toLocalTime() : null)
                .deliveredAt(order.getDeliveredAt() != null ? order.getDeliveredAt().toLocalTime() : null)
                .build();
    }

    //getAllCuisines by user
    public List<CuisineDTO> getAllCuisines(String token) {
        //  Extract email from token safely
        String email;
        try {
            email = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            throw new CustomException("Invalid or expired token");
        }

        if (email == null || email.isEmpty()) {
            throw new CustomException("Invalid token");
        }

        //  Check if user exists
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        //  Fetch all available cuisines
        return cuisineRepo.findAll().stream()
                .filter(Cuisine::getIsAvailable) // Only available cuisines
                .map(c -> CuisineDTO.builder()
                        .cuisineId(c.getCuisineId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .photo(c.getPhoto())
                        .stateName(c.getStateName())
                        .build())
                .collect(Collectors.toList());
    }

    // getAllMealsByStateName
   /** public List<MealWithCuisineResponse> getAllMealsByStateName(String stateName) {

        List<CloudKitchenMeal> meals = cloudKitchenMealRepo.findAllByCloudKitchen_State(stateName);

        return meals.stream()
                .filter(meal -> meal != null && meal.getMeal() != null && meal.getCloudKitchen() != null)
                .map(meal -> {
                    String cloudKitchenName = meal.getCloudKitchen().getCity() + "_" + meal.getCloudKitchen().getDivision();
                    double basePrice = meal.getMeal().getPrice();
                    double discountedPrice = basePrice; // no subscription/offer logic here, just base price

                    return new MealWithCuisineResponse(
                            meal.getMeal().getMealId(),
                            meal.getMeal().getName(),
                            basePrice,
                            discountedPrice,
                            meal.getMeal().getPhotos(),
                            meal.getMeal().getNutritionalInformation(),
                            meal.getMeal().getDescription(),
                            meal.getMeal().getCuisine() != null ? meal.getMeal().getCuisine().getCuisineId() : null,
                            meal.getMeal().getCuisine() != null ? meal.getMeal().getCuisine().getName() : null,
                            meal.getCloudKitchen().getCloudKitchenId(),
                            cloudKitchenName
                    );
                })
                .collect(Collectors.toList());
    }**/


//

   public List<MealWithCuisineResponse> getAllMealsByStateName(String token, String stateName) {

       String jwtToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : token;

       final boolean hasActiveSubscription;
       boolean subscriptionActive = false;

       if (jwtToken != null && !jwtToken.isEmpty()) {
           try {
               String userEmail = jwtUtil.extractUsername(jwtToken);
               if (userEmail != null && !userEmail.isEmpty()) {
                   User user = userRepo.findByEmail(userEmail)
                           .orElseThrow(() -> new CustomException("User not found"));

                   subscriptionActive = subscriptionRepo
                           .findFirstByUserAndEndDateAfter(user, LocalDateTime.now())
                           .isPresent();
               }
           } catch (Exception ignored) {}
       }

       hasActiveSubscription = subscriptionActive;

       LocalDate today = LocalDate.now();
       LocalDate firstOfMonth = today.withDayOfMonth(1);
       LocalDate firstWednesday = firstOfMonth;
       while (firstWednesday.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
           firstWednesday = firstWednesday.plusDays(1);
       }
       LocalDate secondWednesday = firstWednesday.plusDays(7);
       boolean isOfferDay = today.equals(secondWednesday);

       List<CloudKitchenMeal> availableMeals = cloudKitchenMealRepo.findAllAvailableFromActiveKitchens();

       return availableMeals.stream()
               .filter(meal -> meal != null
                       && meal.getMeal() != null
                       && meal.getCloudKitchen() != null
                       && meal.getMeal().getCuisine() != null
                       && meal.getMeal().getCuisine().getStateName() != null
                       && meal.getMeal().getCuisine().getStateName().equalsIgnoreCase(stateName))
               .map(meal -> {
                   String cloudKitchenName = meal.getCloudKitchen().getCity() + "_" + meal.getCloudKitchen().getDivision();
                   double basePrice = meal.getMeal().getPrice();
                   double finalPrice;

                   if (hasActiveSubscription) {
                       finalPrice = 0.0; // 💥 price is zero for subscribed users
                   } else if (isOfferDay) {
                       finalPrice = basePrice * (1 - OFFER_DISCOUNT_PERCENT / 100);
                   } else {
                       finalPrice = basePrice;
                   }

                   return new MealWithCuisineResponse(
                           meal.getMeal().getMealId(),
                           meal.getMeal().getName(),
                           basePrice,
                           finalPrice,
                           meal.getMeal().getPhotos(),
                           meal.getMeal().getNutritionalInformation(),
                           meal.getMeal().getDescription(),
                           meal.getMeal().getCuisine().getCuisineId(),
                           meal.getMeal().getCuisine().getName(),
                           meal.getCloudKitchen().getCloudKitchenId(),
                           cloudKitchenName,
                           meal.getCloudKitchen().getIsOpen()
                   );
               })
               .collect(Collectors.toList());
   }


    //getAllCloudKitchenNames
    public List<String> getAllCloudKitchenNames(String token) {
        // 1. Extract user email from token
        String email = jwtUtil.extractUsername(token.startsWith("Bearer ") ? token.substring(7) : token);

        if (email == null || email.isEmpty()) {
            throw new CustomException("Invalid or expired token");
        }

        // 2. Check if user exists
        userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        // 3. Fetch all active cloud kitchens
        return cloudKitchenRepo.findAll().stream()
                .filter(CloudKitchen::getIsActive)
                .map(ck -> ck.getCity() + "_" + ck.getDivision())
                .collect(Collectors.toList());
    }



}
