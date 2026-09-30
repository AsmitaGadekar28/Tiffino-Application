package com.tiffino.tiffino.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPersonDTO {
    private Long deliveryPersonId;
    private String name;
    private String email;
    private String phoneNo;
    private String cloudKitchenId;
    private String managerId;
    private String otp;
    
}
