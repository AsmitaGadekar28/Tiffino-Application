package com.tiffino.tiffino.service;

import com.tiffino.tiffino.dto.CloudKitchenDTO;
import com.tiffino.tiffino.dto.CuisineMealDTO;
import com.tiffino.tiffino.dto.DeliveryPersonDTO;
import com.tiffino.tiffino.dto.ManagerIssueDto;
import com.tiffino.tiffino.entity.DeliveryPerson;
import com.tiffino.tiffino.entity.Order;
import com.tiffino.tiffino.entity.request.ChangePasswordRequest;
import com.tiffino.tiffino.entity.request.ManagerRequest;
import com.tiffino.tiffino.entity.response.AssignOrderResponse;
import com.tiffino.tiffino.entity.response.LoginResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface IManagerService {

    // Forgot password - send OTP
   //String forgotPassword(String email);

    // Change password (after OTP verification)
   //String changePassword(ChangePasswordRequest request);

    // Update password using OTP + newPassword (no email required)
    //String updatePassword(String otp, String newPassword);

    // Get cloud kitchen data for logged-in manager
    CloudKitchenDTO getDataOfCloudKitchen();

    // Add or remove meal from manager's menu
    String addOrRemoveMeals(Long mealId);

    // Get all cuisines & meals
    List<CuisineMealDTO> getAllCuisinesAndMeals();


    // Get all meals available for manager's cloud kitchen
    List<CuisineMealDTO> getAllMealsWithCloudKitchen();

    AssignOrderResponse assignOrderToDeliveryPerson(Long orderId, Long deliveryPersonId);

    List<DeliveryPersonDTO> getAvailableDeliveryPersons();

    String openAndCloseCloudKitchen();

   // new chatboat repo
   List<ManagerIssueDto> getAllIssuesForManager();

}
