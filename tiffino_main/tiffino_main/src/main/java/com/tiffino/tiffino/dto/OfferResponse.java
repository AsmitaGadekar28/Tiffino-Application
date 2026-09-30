package com.tiffino.tiffino.dto;


import com.tiffino.tiffino.entity.response.MealWithCuisineResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class OfferResponse {
    private String message;
    private String offerDate;        // YYYY-MM-DD
    private double discountPercent;
    private List<MealWithCuisineResponse> meals;
}

