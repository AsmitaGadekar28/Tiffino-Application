package com.tiffino.tiffino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public  class MealResponseDTO {
        private Long mealId;
        private String mealName;
        private boolean isSelected = false;
    }

