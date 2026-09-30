package com.tiffino.tiffino.entity.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrderMealResponse {
    private String mealName;
    private String mealPhoto;
    private Integer quantity;
    private Double mealPrice;
}
