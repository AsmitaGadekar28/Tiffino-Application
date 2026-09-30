package com.tiffino.tiffino.entity.response;

import com.tiffino.tiffino.dto.ReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloudKitchenReviewResponse {
    private String cloudKitchenId;
    private String  managerId;
    private String state;
    private String city;
    private String division;
    private List<ReviewResponse> reviews;  // <-- must match the list type
}
