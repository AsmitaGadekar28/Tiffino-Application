package com.tiffino.tiffino.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private String cloudKitchenId;
    private String cloudKitchenName; // city-division
    private List<CartItemResponse> items;
    private List<String> allergies;
    private Double totalAmount;
    private boolean hasSubscription;
}
