package com.tiffino.tiffino.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilterResponseDTO {
    private String cloudKitchenId;
    private String city;
    private String division;
    private Boolean isActive;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private String managerId;
    private String managerName;
    private Boolean managerIsActive;
    private Boolean managerIsDeleted;
    private LocalDateTime managerCreatedAt;
}
