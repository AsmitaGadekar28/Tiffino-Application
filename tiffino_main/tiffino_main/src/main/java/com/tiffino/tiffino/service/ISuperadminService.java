package com.tiffino.tiffino.service;

import com.tiffino.tiffino.dto.ManagerCloudKitchenDTO;
import com.tiffino.tiffino.entity.Manager;

import java.util.List;

public interface ISuperadminService {
    List<ManagerCloudKitchenDTO> getAllManagersWithCloudKitchen();
    String deleteManagerById(String managerId);
    String deleteCloudKitchenById(String cloudKitchenId);



}
