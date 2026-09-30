package com.tiffino.tiffino.entity.response;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MealResponse {
    private Long mealId;
    private String mealName;
    private Double price;
    private String photo;
    private String nutritionalInformation;
    private String description;
    private String cuisineName;
}
