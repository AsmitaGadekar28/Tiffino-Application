package com.tiffino.tiffino.entity.response;

import lombok.*;
import java.time.LocalTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackOrderResponse {
    private Long orderId;
    private String orderStatus;
    private String deliveryPersonName;
    private String deliveryPersonPhoneNo;
    private LocalTime assignedAt;
    private LocalTime pickUpAt;
    private LocalTime deliveredAt;
}

