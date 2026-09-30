package com.tiffino.tiffino.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponse {
    private String message;
    private Long orderId;
    private double totalAmount;
    private LocalDateTime orderTime;
    private String orderDate;
}
