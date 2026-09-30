package com.tiffino.tiffino.entity.response;

import com.tiffino.tiffino.dto.CartResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartUpdateResponse {
    private CartResponse cart;
    private String message;
}
