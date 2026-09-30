package com.tiffino.tiffino.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftCardDto {
    private Long id;
    private String giftCardCode;
    private String validForPlan;
    private double discountPercentage;
    private String description;
}
