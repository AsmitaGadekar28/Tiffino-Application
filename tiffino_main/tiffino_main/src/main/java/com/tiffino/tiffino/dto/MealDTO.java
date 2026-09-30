package com.tiffino.tiffino.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MealDTO {
    private Long mealId;
    private String name;
    private String description;
    private String nutritionalInformation;
    private MultipartFile photo; // upload only
    private double price;
    private Long cuisineId; // relation
}
