package com.tiffino.tiffino.entity.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloudKitchenRequest {
    private String cloudKitchenId;
    private String state;
    private String city;
    private String division;
    private String address;
    private Integer pinCode;
}
