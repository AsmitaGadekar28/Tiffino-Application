package com.tiffino.tiffino.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MealByStateResponse {
    private Long mealId;
    private String mealName;
    private Double basePrice;
    private Double discountedPrice;
    private String photo;
    private String nutritionalInformation;
    private String description;
    private Long cuisineId;
    private String cuisineName;
    private String cloudKitchenId;
    private String cloudKitchenName;
    private Boolean isOpen;
}
