package com.tiffino.tiffino.entity.request;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long orderId;            // Order for which review is submitted
    private String cloudKitchenReview; // The review/comment text
    private Integer rating;          // Rating (1-5)
}
