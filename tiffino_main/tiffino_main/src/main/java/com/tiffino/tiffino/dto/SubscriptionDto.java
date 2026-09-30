package com.tiffino.tiffino.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {
    private double appliedDiscountPercent;
    private double originalPrice;      // price before discount
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private String durationType;
    private int caloriesPerMeal;
    private Long userSubId;            // subscription id
    private double finalPrice;         // price after discount
}
