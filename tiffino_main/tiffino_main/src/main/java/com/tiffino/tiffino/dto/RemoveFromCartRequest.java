package com.tiffino.tiffino.dto;

import lombok.Data;

import java.util.List;

@Data
public class RemoveFromCartRequest {
    private List<Long> mealIds; // can pass single or multiple
}
