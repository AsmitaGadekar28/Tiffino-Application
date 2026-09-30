package com.tiffino.tiffino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudKitchenDTO {
    private String cloudKitchenId;
    private String division;
    private String address;
    private String city;
    private String state;
    private Integer pincode;
    private String ManagerId;

}
