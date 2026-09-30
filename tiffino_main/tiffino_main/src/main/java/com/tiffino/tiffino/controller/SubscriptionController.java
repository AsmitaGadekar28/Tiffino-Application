package com.tiffino.tiffino.controller;

import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.dto.GiftCardDto;
import com.tiffino.tiffino.entity.User;
import com.tiffino.tiffino.entity.request.SubscriptionRequest;
import com.tiffino.tiffino.entity.response.GiftCardResponse;
import com.tiffino.tiffino.entity.response.SubscriptionResponse;
import com.tiffino.tiffino.repository.UserRepo;
import com.tiffino.tiffino.service.SubscriptionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    public SubscriptionController(SubscriptionService subscriptionService,
                                  JwtUtil jwtUtil,
                                  UserRepo userRepo) {
        this.subscriptionService = subscriptionService;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    // Assign subscription to user (userId comes from JWT)
    @PostMapping(value = "/assignSubscriptionToUser", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubscriptionResponse> assignSubscription(
            @RequestHeader("Authorization") String authHeader, // "Bearer <token>"
            @RequestParam String planType,
            @RequestParam(required = false) List<String> mealTimes,
            @RequestParam(required = false) List<String> allergies,
            @RequestParam Integer caloriesPerMeal,
            @RequestPart(required = false) MultipartFile dietaryFile,
            @RequestParam(required = false) String giftCardCodeInput
    ) {

        // 1️⃣ Extract token and then user email
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        // 2️⃣ Find userId from email
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Build SubscriptionRequest
        SubscriptionRequest req = new SubscriptionRequest();
        req.setPlanType(planType);
        req.setMealTimes(mealTimes);
        req.setAllergies(allergies);
        req.setCaloriesPerMeal(caloriesPerMeal);
        req.setDietaryFile(dietaryFile);
        req.setGiftCardCodeInput(giftCardCodeInput);

        // 4️⃣ Call updated service method
        SubscriptionResponse resp = subscriptionService.assignSubscriptionToUser(user.getUserId(), req);

        return ResponseEntity.ok(resp);
    }

/**
    // Get all gift cards of user
    @GetMapping("/getAllGiftCardsOfUser")
    public ResponseEntity<List<GiftCardDto>> getAllGiftCards(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GiftCardDto> giftCards = subscriptionService.getAllGiftCardsOfUser(user.getUserId());

        return ResponseEntity.ok(giftCards);
    }**/
@GetMapping("/getAllGiftCardsOfUser")
public ResponseEntity<List<GiftCardResponse>> getAllGiftCards() {
    List<GiftCardResponse> giftCards = subscriptionService.getAllGiftCardsOfUser();
    return ResponseEntity.ok(giftCards);
}


}