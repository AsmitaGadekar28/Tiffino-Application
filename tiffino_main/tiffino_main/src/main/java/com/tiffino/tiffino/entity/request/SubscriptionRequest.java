package com.tiffino.tiffino.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequest {
    private String planType;
    private List<String> mealTimes;
    private List<String> allergies;
    private int caloriesPerMeal;
    private MultipartFile dietaryFile;
    private String giftCardCodeInput;
}

