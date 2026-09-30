package com.tiffino.tiffino.entity.response;

/**
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GiftCardResponse {
    private Long userGiftCardId;
    private String validForPlan;
    private String giftCardCode;
    private double discountPercentage;
    private String description;
}

**/


import lombok.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftCardResponse {
    private Long id;
    private String code;
    private double discountPercent;
    private String validForPlan;
    private String description;
    private boolean active;
    private LocalDate generatedDate;
}
