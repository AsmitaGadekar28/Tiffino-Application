package com.tiffino.tiffino.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Long mealId;
    private String mealName;
    private String mealPhoto;
    private Integer qty; // quantity of this meal
    private Double basePrice; // original meal price
    private Double discountedPrice; // after 20% discount if applicable
    private Double lineTotal; // discountedPrice * qty
}
