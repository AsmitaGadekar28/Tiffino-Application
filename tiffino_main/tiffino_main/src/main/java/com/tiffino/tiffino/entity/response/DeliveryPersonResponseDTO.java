package com.tiffino.tiffino.entity.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryPersonResponseDTO {
    private  Long deliveryPersonId;
    private String name;
    private String email;
    private String password;
    private String phoneNo;
    private String cloudKitchenId;
    private String managerId;
    private String otp;
}
