package com.tiffino.tiffino.entity.request;



import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private Long orderId;
    private String status;
}

