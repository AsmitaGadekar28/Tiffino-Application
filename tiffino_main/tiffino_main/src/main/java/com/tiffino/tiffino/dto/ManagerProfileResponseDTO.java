package com.tiffino.tiffino.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManagerProfileResponseDTO {
    private String managerId;
    private String managerName;
    private String managerEmail;
    private String dob;
    private String phoneNo;
    private String currentAddress;
    private String permanentAddress;
    private String adharCard;
    private String panCard;
    private String photo;
    private String city;
    private Boolean isActive;
    private Boolean isDeleted;

    private CloudKitchenDTO cloudKitchen;
}

