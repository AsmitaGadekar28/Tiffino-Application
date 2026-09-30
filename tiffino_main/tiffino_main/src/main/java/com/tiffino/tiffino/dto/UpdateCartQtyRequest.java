package com.tiffino.tiffino.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCartQtyRequest {
    private List<CartItemQtyUpdate> items;
}
