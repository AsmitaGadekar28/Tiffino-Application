package com.tiffino.tiffino.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MealWithCuisineResponse {

    private Long mealId;
    private String mealName;
    private Double basePrice;          // original price
    private Double discountedPrice;    // after 20% discount if subscription active
    private String photo;
    private String nutritionalInformation;
    private String description;

    private Long cuisineId;
    private String cuisineName;


    private String cloudKitchenId;
    private String cloudKitchenName;
    private Boolean isOpen;
}
