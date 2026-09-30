package com.tiffino.tiffino.entity.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryPersonRequest {
    private String name;
    private String email;
    private String password;
    private String phoneNo;
    private String cloudKitchenId;
}
