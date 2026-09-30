package com.tiffino.tiffino.controller;
import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.dto.CuisineDTO;
import com.tiffino.tiffino.entity.User;
import com.tiffino.tiffino.entity.request.ReviewRequest;
import com.tiffino.tiffino.entity.request.UpdateUserRequest;
import com.tiffino.tiffino.entity.response.MealByStateResponse;
import com.tiffino.tiffino.entity.response.MealWithCuisineResponse;
import com.tiffino.tiffino.entity.response.TrackOrderResponse;
import com.tiffino.tiffino.repository.UserRepo;
import com.tiffino.tiffino.service.CuisineService;
import com.tiffino.tiffino.service.SubscriptionService;
import com.tiffino.tiffino.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final SubscriptionService subscriptionService;

    private final CuisineService cuisineService;

    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;

    // Get all available meals with subscription discount
//    @GetMapping("/getAllAvailableMealsWithCuisine")
//    public ResponseEntity<List<MealWithCuisineResponse>> getAllAvailableMeals(
//            @RequestHeader(value = "Authorization", required = false) String authHeader) {
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        String token = authHeader.substring(7).trim(); // remove "Bearer " and trim
//        List<MealWithCuisineResponse> meals = userService.getAllAvailableMealsWithCuisine(token);
//        return ResponseEntity.ok(meals);
//    }


    //update user
    @PutMapping("/updateUser")
    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String token,
                                           @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.updateUser(token, request);
        return ResponseEntity.ok(updatedUser);
    }

    //get offer
    @GetMapping("/getOffer")
    public ResponseEntity<?> getOffer() {
        return ResponseEntity.ok(userService.getOfferMessage());
    }


    //  Cancel Order
    @PostMapping("/cancelOrder/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId,
                                              @RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.extractUsername(jwt);
        String message = userService.cancelOrder(orderId, email);
        return ResponseEntity.ok(message);
    }

    // Create Review
    @PostMapping("/createReview")
    public ResponseEntity<String> createReview(@RequestBody ReviewRequest request,
                                               @RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.extractUsername(jwt);
        String message = userService.createReview(request, email);
        return ResponseEntity.ok(message);
    }
    // track order
    @GetMapping("/trackOrder/{orderId}")
    public ResponseEntity<TrackOrderResponse> trackOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        String token = authHeader.substring(7).trim();

        String email = jwtUtil.extractUsername(token);
        TrackOrderResponse response = userService.trackOrder(orderId, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllCuisines")
    public ResponseEntity<List<CuisineDTO>> getAllCuisines(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7).trim();
        List<CuisineDTO> cuisines = userService.getAllCuisines(token);
        return ResponseEntity.ok(cuisines);
    }


    //getAllMealsByStateName
//    @GetMapping("/meals/state/{stateName}")
//    public ResponseEntity<List<MealWithCuisineResponse>> getMealsByState(
//            @RequestHeader(value = "Authorization", required = false) String token,
//            @PathVariable String stateName) {
//        List<MealWithCuisineResponse> meals = userService.getAllMealsByStateName(token, stateName);
//        return ResponseEntity.ok(meals);
//    }

    //getAllCloudkitchennames
    @GetMapping("/cloudkitchens/names")
    public ResponseEntity<List<String>> getAllCloudKitchenNames(@RequestHeader("Authorization") String token) {
        List<String> names = userService.getAllCloudKitchenNames(token);
        return ResponseEntity.ok(names);
    }

    // Get all available meals (subscription-aware)
    @GetMapping("/getAllAvailableMealsWithCuisine")
    public ResponseEntity<List<MealWithCuisineResponse>> getAllAvailableMeals(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        }

        // token can be null — service will handle both cases
        List<MealWithCuisineResponse> meals = userService.getAllAvailableMealsWithCuisine(token);
        return ResponseEntity.ok(meals);
    }


    // Get all meals by state (subscription-aware)
    @GetMapping("/meals/state/{stateName}")
    public ResponseEntity<List<MealWithCuisineResponse>> getMealsByState(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String stateName) {

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        }

        List<MealWithCuisineResponse> meals = userService.getAllMealsByStateName(token, stateName);
        return ResponseEntity.ok(meals);
    }

}
