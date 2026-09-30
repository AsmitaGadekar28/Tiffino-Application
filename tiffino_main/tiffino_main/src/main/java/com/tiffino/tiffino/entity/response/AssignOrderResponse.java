package com.tiffino.tiffino.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignOrderResponse {
    private Long orderId;
    private Long deliveryPersonId;
    private String message;
}
