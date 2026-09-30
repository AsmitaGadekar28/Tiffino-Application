package com.tiffino.tiffino.entity.response;



import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CloudKitchenWithMealsResponse {
    private String cloudKitchenId;
    private String cloudKitchenName;
    private List<MealResponse> meals;
}

