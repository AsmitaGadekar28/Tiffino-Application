package com.tiffino.tiffino.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddToCartRequest {
    private String cloudKitchenId;
    private List<Long> mealIds;
}
