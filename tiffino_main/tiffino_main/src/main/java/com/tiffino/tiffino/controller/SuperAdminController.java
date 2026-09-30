package com.tiffino.tiffino.controller;

import com.tiffino.tiffino.dto.*;
import com.tiffino.tiffino.entity.CloudKitchen;
import com.tiffino.tiffino.entity.request.CloudKitchenRequest;
import com.tiffino.tiffino.entity.request.DeliveryPersonRequest;
import com.tiffino.tiffino.entity.request.ManagerRequest;
import com.tiffino.tiffino.entity.request.SuperAdminRequest;
import com.tiffino.tiffino.entity.response.CloudKitchenReviewResponse;
import com.tiffino.tiffino.entity.response.SubscriberUserResponse;
import com.tiffino.tiffino.entity.response.SuperAdminResponse;
import com.tiffino.tiffino.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/superadmin")
@SecurityRequirement(name = "bearerAuth")
public class SuperAdminController {

    @Autowired
    private SuperAdminService superAdminService;

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    @Autowired
    private ISuperadminService iSuperadminService;

    @Autowired
    private CuisineService cuisineService;

    @Autowired
    private MealService mealService;

    // Update SuperAdmin
    @PutMapping("/update")
    public ResponseEntity<SuperAdminResponse> updateSuperAdmin(@RequestBody SuperAdminRequest request) {
        return ResponseEntity.ok(superAdminService.updateSuperAdmin(request));
    }

    // Save CloudKitchen
    @PostMapping("/saveCloudKitchen")
    public ResponseEntity<CloudKitchen> saveCloudKitchen(@RequestBody CloudKitchenRequest request) {
        return new ResponseEntity<>(superAdminService.saveCloudKitchen(request), HttpStatus.CREATED);
    }

// Manager save + OTP send
@PostMapping(value = "/saveManager", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public String saveManager(@ModelAttribute ManagerRequest request) {
    return superAdminService.saveManagerWithOtp(request);
}


//getAllManagerWithCloudKitchen
    @GetMapping("/getAllManagersWithCloudKitchen")
    public ResponseEntity<List<ManagerCloudKitchenDTO>> getAllManagersWithCloudKitchen() {
        List<ManagerCloudKitchenDTO> managersWithKitchen = iSuperadminService.getAllManagersWithCloudKitchen();
        return ResponseEntity.ok(managersWithKitchen);
    }

    //delete manager by id
    @DeleteMapping("/deleteManager/{managerId}")
    public ResponseEntity<String> deleteManager(@PathVariable String managerId) {
        return ResponseEntity.ok(iSuperadminService.deleteManagerById(managerId));
    }

    //delete clodkitchen by id
    @DeleteMapping("/deleteCloudKitchen/{cloudKitchenId}")
    public ResponseEntity<String> deleteCloudKitchen(@PathVariable String cloudKitchenId) {
        return ResponseEntity.ok(iSuperadminService.deleteCloudKitchenById(cloudKitchenId));
    }

    //save delivery person
    @PostMapping("/saveDeliveryPerson/{cloudKitchenId}")
    public ResponseEntity<DeliveryPersonDTO> saveDeliveryPerson(
            @RequestBody DeliveryPersonRequest request,
            @PathVariable String cloudKitchenId) {

        DeliveryPersonDTO dto = superAdminService.saveDeliveryPerson(request, cloudKitchenId);
        return ResponseEntity.ok(dto);
    }


    //save meal
    @PostMapping(value = "/saveMeals",consumes = "multipart/form-data")
    public ResponseEntity<MealDTO> saveMeal(@ModelAttribute MealDTO dto){
     MealDTO saveMeal = mealService.createMeal(dto);
     return  ResponseEntity.ok(saveMeal);
    }



    @GetMapping("/subscribers")
    public ResponseEntity<List<SubscriberUserResponse>> getAllSubscribers() {
        List<SubscriberUserResponse> response = superAdminService.getAllSubscriberUsers();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saveCuisines")
    public ResponseEntity<CuisineDTO> saveCuisine(@ModelAttribute CuisineDTO dto) throws IOException {
        CuisineDTO saved = cuisineService.saveCuisine(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/allCuisines")
    public ResponseEntity<List<CuisineDTO>> getAllCuisines() {
        List<CuisineDTO> cuisines = cuisineService.getAllCuisines();
        return ResponseEntity.ok(cuisines);
    }

    // ✅ Get all CloudKitchens with Manager & Reviews
    @GetMapping("/getAllCloudKitchensAndReviews")
    public ResponseEntity<List<CloudKitchenReviewResponse>> getAllCloudKitchensAndReviews() {
        List<CloudKitchenReviewResponse> response = superAdminService.getAllCloudKitchensAndReviews();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/searchFilter")
    public ResponseEntity<List<SearchFilterResponseDTO>> searchFilter(@RequestBody SearchFilterRequestDTO request) {
        return ResponseEntity.ok(superAdminService.searchFilter(request));
    }

}
