package com.tiffino.tiffino.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManagerCloudKitchenDTO {
    private String managerId;
    private String managerName;
    private String managerEmail;
    private String cloudKitchenId;  // CloudKitchen ID
    private String cloudKitchenCity; // CloudKitchen City
}
