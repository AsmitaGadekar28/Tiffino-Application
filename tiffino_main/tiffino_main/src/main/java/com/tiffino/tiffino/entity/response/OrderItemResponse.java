package com.tiffino.tiffino.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private String mealName;
    private String mealPhoto;
    private int quantity;
    private double price;         // discounted price
    private double originalPrice; // original price
}
