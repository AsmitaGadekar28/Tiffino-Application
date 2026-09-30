package com.tiffino.tiffino.controller;

import com.tiffino.tiffino.dto.CloudKitchenDTO;
import com.tiffino.tiffino.dto.CuisineMealDTO;
import com.tiffino.tiffino.dto.DeliveryPersonDTO;
import com.tiffino.tiffino.dto.ManagerIssueDto;
import com.tiffino.tiffino.entity.request.ChangePasswordRequest;
import com.tiffino.tiffino.entity.request.PasswordRequest;
import com.tiffino.tiffino.entity.response.AssignOrderResponse;
import com.tiffino.tiffino.service.IManagerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager")
@SecurityRequirement(name = "bearerAuth")
public class managerController {

    @Autowired
    private IManagerService managerService;

    /**
   // Forgot Password → send OTP
  @PostMapping("/forgotPassword")
  public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request){
      String email = request.get("email");
      return ResponseEntity.ok(managerService.forgotPassword(email));
  }


    //  Change Password → OTP + new password + confirm password
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest req) {
        String message = managerService.changePassword(req);
        return ResponseEntity.ok(message);
    }

    // Update password using OTP
    /**@PostMapping("/updateManager")
    public ResponseEntity<?> updateManagerPassword(@RequestBody PasswordRequest request) {
        String response = managerService.updateManager(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()
        );
        return ResponseEntity.ok(response);
    }**/

    // Update password using OTP (no email needed)
   /** @PostMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordRequest request) {
        String response = managerService.updateManager(request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok(response);
    }**/

   /**@PostMapping("/updatePassword")
   public ResponseEntity<String> updatePassword(@RequestBody PasswordRequest request) {
       String response = managerService.updatePassword(request.getOtp(), request.getNewPassword());
       return ResponseEntity.ok(response);
   }
**/

    // Get all cuisines & meals (full list, no add/remove effect)
    @GetMapping("/getAllCuisinesAndMeals")
    public ResponseEntity<List<CuisineMealDTO>> getAllCuisinesAndMeals() {
        List<CuisineMealDTO> list = managerService.getAllCuisinesAndMeals();
        return ResponseEntity.ok(list);
    }

    // Get cloud kitchen data for logged-in manager
    @GetMapping("/getDataOfCloudKitchen")
    public ResponseEntity<CloudKitchenDTO> getDataOfCloudKitchen() {
        CloudKitchenDTO data = managerService.getDataOfCloudKitchen();
        return ResponseEntity.ok(data);
    }


// Get all meals with cloud kitchen availability for logged-in manager
@GetMapping("/getAllMealsWithCloudKitchen")
public ResponseEntity<List<CuisineMealDTO>> getAllMealsWithCloudKitchen() {
    List<CuisineMealDTO> list = managerService.getAllMealsWithCloudKitchen();
    return ResponseEntity.ok(list);
}

    //  Add or remove meals
    @PostMapping("/addOrRemoveMeals/{mealId}")
    public ResponseEntity<?> addOrRemoveMeals(@PathVariable Long mealId) {
        String response = managerService.addOrRemoveMeals(mealId);
        return ResponseEntity.ok(response);
    }

    //  Assign order to delivery person
    @PostMapping("/assignOrder/{orderId}/{deliveryPersonId}")
    public ResponseEntity<AssignOrderResponse> assignOrder(
            @PathVariable Long orderId,
            @PathVariable Long deliveryPersonId) {
        AssignOrderResponse response = managerService.assignOrderToDeliveryPerson(orderId, deliveryPersonId);
        return ResponseEntity.ok(response);
    }

    // Get all available delivery persons for manager's cloud kitchen
    @GetMapping("/availableDeliveryPersons")
    public ResponseEntity<List<DeliveryPersonDTO>> getAvailableDeliveryPersons() {
        List<DeliveryPersonDTO> list = managerService.getAvailableDeliveryPersons();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/openAndCloseCloudKitchen")
    public ResponseEntity<String> openAndCloseCloudKitchen() {
        String response = managerService.openAndCloseCloudKitchen();
        return ResponseEntity.ok(response);
    }
    
    //New chatboat getallorderquery for manger
    @GetMapping("/getAllOrderQuery")
    public List<ManagerIssueDto> getAllOrderQuery() {
        return managerService.getAllIssuesForManager();
    }



}
