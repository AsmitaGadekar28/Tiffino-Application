package com.tiffino.tiffino.controller;

import com.tiffino.tiffino.entity.User;
import com.tiffino.tiffino.service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user/review")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId,
            Principal principal   // Spring Security injects current logged-in username/email
    ) {
        String email = principal.getName();  // get the logged-in user's email
        String message = reviewService.deleteReviewById(reviewId, email);
        return ResponseEntity.ok(message);
    }

}
