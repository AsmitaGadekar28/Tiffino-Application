package com.tiffino.tiffino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuisineMealDTO {

    private String cuisineName;
    private List<MealResponseDTO> meals;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MealResponseDTO {
        private Long mealId;
        private String mealName;
        private boolean isSelected = false; // default false
    }
}
