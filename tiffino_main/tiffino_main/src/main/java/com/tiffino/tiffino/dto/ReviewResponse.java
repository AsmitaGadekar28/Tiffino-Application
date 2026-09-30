package com.tiffino.tiffino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewResponse {
    private String comment;
    private Integer rating;
}
