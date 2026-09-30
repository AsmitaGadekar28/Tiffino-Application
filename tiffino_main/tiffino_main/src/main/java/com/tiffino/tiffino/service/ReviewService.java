package com.tiffino.tiffino.service;

import com.tiffino.tiffino.entity.Review;
import com.tiffino.tiffino.entity.User;
import com.tiffino.tiffino.repository.ReviewRepo;
import com.tiffino.tiffino.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;


    /**
     * Permanently delete review by ID, only if it belongs to the given email
     */
    public String deleteReviewById(Long reviewId, String userEmail) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new CustomException("Review not found with ID: " + reviewId));

        // ✅ Check ownership
        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new CustomException("You are not authorized to delete this review");
        }

        // ✅ Permanent delete
        reviewRepo.delete(review);

        return "✅ Review deleted permanently with ID: " + reviewId;
    }
}
