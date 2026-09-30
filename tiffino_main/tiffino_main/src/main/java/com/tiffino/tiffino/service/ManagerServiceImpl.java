package com.tiffino.tiffino.service;

import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.dto.*;
import com.tiffino.tiffino.entity.*;
import com.tiffino.tiffino.entity.request.ChangePasswordRequest;
import com.tiffino.tiffino.entity.request.ManagerRequest;
import com.tiffino.tiffino.entity.response.AssignOrderResponse;
import com.tiffino.tiffino.exception.CustomException;
import com.tiffino.tiffino.repository.*;
import com.tiffino.tiffino.util.OtpUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManagerServiceImpl implements IManagerService {

    @Autowired
    private ManagerRepo managerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MealRepo mealRepo;

    @Autowired
    private CloudKitchenMealRepo cloudKitchenMealRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private DeliveryPersonRepo deliveryPersonRepo;

    @Autowired
    private  IssueReportRepository issueRepo;
    
    private final CuisineRepo cuisineRepository;

    public ManagerServiceImpl(CuisineRepo cuisineRepository) {
        this.cuisineRepository = cuisineRepository;
    }
    

/**
    // Send OTP to manager email
    @Override
    public String forgotPassword(String email) {
        Manager manager = managerRepo.findByManagerEmail(email)
                .orElseThrow(() -> new CustomException("Manager not found with email: " + email));

        // Generate OTP
        String otp = OtpUtil.generateOtp();
        manager.setOtp(otp);  // save OTP in DB
        managerRepo.save(manager);

        // Debug: print OTP to console
        System.out.println("Generated OTP for " + email + ": " + otp);

        // Send email (optional for testing)
        String subject = "Manager Password Reset OTP";
        String body = "Hello " + manager.getManagerName() + ",\nYour OTP to reset password is: " + otp;
        emailService.sendEmail(manager.getManagerEmail(), subject, body);

        return "OTP sent! Check console for OTP.";
    }
    // Change password using OTP + confirm password
    @Override
    public String changePassword(ChangePasswordRequest req) {
        Manager manager = managerRepo.findByManagerEmail(req.getEmail())
                .orElseThrow(() -> new CustomException("Manager not found with email: " + req.getEmail()));

        // Validate OTP
        if (req.getOtp() == null || manager.getOtp() == null || !manager.getOtp().equals(req.getOtp().trim())) {
            throw new CustomException("Invalid OTP!");
        }

        // Validate new password and confirm password
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new CustomException("New password and confirm password do not match!");
        }

        // Update password and clear OTP
        manager.setPassword(passwordEncoder.encode(req.getNewPassword()));
        manager.setOtp(null);
        managerRepo.save(manager);

        // Debug: confirm success in console
        System.out.println("Password changed successfully for " + req.getEmail());

        return "Password changed successfully!";
    }

@Override
public String updatePassword(String otp, String newPassword) {
    // Find manager by OTP
    Manager manager = managerRepo.findByOtp(otp)
            .orElseThrow(() -> new CustomException("Invalid OTP"));

    // Update password
    manager.setPassword(passwordEncoder.encode(newPassword));

    // Clear OTP so it cannot be reused
    manager.setOtp(null);

    managerRepo.save(manager);

    return "Password updated successfully!";
}


    // Helper method to validate OTP
    private void validateOtp(Manager manager, String otpInput) {
        if (otpInput == null || manager.getOtp() == null || !manager.getOtp().equals(otpInput.trim())) {
            throw new CustomException("Invalid OTP!");
        }
    }

    // Helper method to clear OTP after use
    private void clearOtp(Manager manager) {
        manager.setOtp(null);
        managerRepo.save(manager);
    }

**/

    //getAllCuisineMeals
  @Override
  @Transactional
  public List<CuisineMealDTO> getAllCuisinesAndMeals() {
      // Full menu, no filter
      return cuisineRepository.findAll().stream()
              .map(cuisine -> {
                  List<CuisineMealDTO.MealResponseDTO> meals =
                          cuisine.getMeals().stream()
                                  .map(meal -> new CuisineMealDTO.MealResponseDTO(
                                          meal.getMealId(),
                                          meal.getName(),
                                          true // all meals selected by default
                                  ))
                                  .toList();

                  return new CuisineMealDTO(cuisine.getName(), meals);
              })
              .toList();
  }


    //  Get cloud kitchen data for logged-in manager
    @Override
    public CloudKitchenDTO getDataOfCloudKitchen() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager manager = managerRepo.findByManagerEmail(email)
                .orElseThrow(() -> new CustomException("Manager not found"));

        if (manager.getCloudKitchen() == null) {
            throw new CustomException("Cloud Kitchen not assigned to manager");
        }

        var ck = manager.getCloudKitchen();
        return new CloudKitchenDTO(
                ck.getCloudKitchenId(),
                ck.getDivision(),
                ck.getAddress(),
                ck.getCity(),
                ck.getState(),
                ck.getPinCode(),
                manager.getManagerId()
        );
    }

    //  Add or remove meals
  @Override
  public String addOrRemoveMeals(Long mealId) {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      Manager manager = managerRepo.findByManagerEmail(email)
              .orElseThrow(() -> new CustomException("Manager not found"));

      if (manager.getCloudKitchen() == null) {
          throw new CustomException("Manager has no cloud kitchen assigned");
      }

      Meal meal = mealRepo.findById(mealId)
              .orElseThrow(() -> new CustomException("Meal not found"));

      // Check if mapping exists
      CloudKitchenMeal mapping = cloudKitchenMealRepo
              .findByCloudKitchenAndMealAndManager(manager.getCloudKitchen(), meal, manager)
              .orElse(null);

      if (mapping == null) {
          // First time, create mapping with available = true
          mapping = new CloudKitchenMeal(manager.getCloudKitchen(), meal, true, manager);
      } else {
          // Toggle only if mapping already exists
          mapping.setAvailable(!mapping.isAvailable());
      }

      cloudKitchenMealRepo.save(mapping);

      return mapping.isAvailable() ? "Meal added successfully" : "Meal removed successfully";
  }


@Override
@Transactional
public List<CuisineMealDTO> getAllMealsWithCloudKitchen() {
    // Logged-in manager ka email
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    Manager manager = managerRepo.findByManagerEmail(email)
            .orElseThrow(() -> new CustomException("Manager not found"));

    if (manager.getCloudKitchen() == null) {
        throw new CustomException("Cloud Kitchen not assigned");
    }

    return cuisineRepository.findAll().stream()
            .map(cuisine -> {
                List<CuisineMealDTO.MealResponseDTO> meals = cuisine.getMeals().stream()
                        .map(meal -> {
                            // Manager-specific mapping
                            CloudKitchenMeal mapping = cloudKitchenMealRepo
                                    .findByCloudKitchenAndMealAndManager(manager.getCloudKitchen(), meal, manager)
                                    .orElseGet(() -> {

                                        CloudKitchenMeal newMapping = new CloudKitchenMeal(manager.getCloudKitchen(), meal, true, manager);
                                        cloudKitchenMealRepo.save(newMapping);
                                        return newMapping;
                                    });

                            return new CuisineMealDTO.MealResponseDTO(
                                    meal.getMealId(),
                                    meal.getName(),
                                    mapping.isAvailable()
                            );
                        })
                        .filter(CuisineMealDTO.MealResponseDTO::isSelected)
                        .toList();

                return new CuisineMealDTO(cuisine.getName(), meals);
            })
            .filter(c -> !c.getMeals().isEmpty()) //
            .toList();
}

//assignOrderToDeliveryPerson
    @Override
    @Transactional
    public AssignOrderResponse assignOrderToDeliveryPerson(Long orderId, Long deliveryPersonId) {
        //  Get logged-in manager
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager manager = managerRepo.findByManagerEmail(email)
                .orElseThrow(() -> new CustomException("Manager not found"));

        CloudKitchen kitchen = manager.getCloudKitchen();
        if (kitchen == null) throw new CustomException("No Cloud Kitchen assigned to manager");

        //  Find Order directly via OrderRepo
        Order order = orderRepo.findById(orderId)
                .filter(o -> o.getCloudKitchen().equals(kitchen))
                .orElseThrow(() -> new CustomException("Order not found for your Cloud Kitchen"));

        if (!order.getStatus().equals("PENDING")) {
            throw new CustomException("Order cannot be assigned. Current status: " + order.getStatus());
        }

        //  Find Delivery Person
        DeliveryPerson dp = deliveryPersonRepo.findById(deliveryPersonId)
                .orElseThrow(() -> new CustomException("Delivery person not found"));

        if (!dp.getIsAvailable() || !dp.getCloudKitchen().equals(kitchen)) {
            throw new CustomException("Delivery person unavailable or belongs to another kitchen");
        }

        //  Assign order and mark delivery person unavailable
        order.setDeliveryPerson(dp);
        order.setStatus("ASSIGNED");
        dp.setIsAvailable(false);

        // Save updates
        deliveryPersonRepo.save(dp);
        order.setAssignedAt(LocalDateTime.now());
        orderRepo.save(order);

        //  Send email notification
        String subject = "New Order Assigned";
        String body = "Hello " + dp.getName() + ",\nYou have been assigned Order ID: " + order.getOrderId();
        emailService.sendEmail(dp.getEmail(), subject, body);

        return new AssignOrderResponse(order.getOrderId(), dp.getDeliveryPersonId(), "Order assigned successfully");
    }

    //getAvailableDeliveryPersons
@Override
public List<DeliveryPersonDTO> getAvailableDeliveryPersons() {
    // Get logged-in manager
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    Manager manager = managerRepo.findByManagerEmail(email)
            .orElseThrow(() -> new CustomException("Manager not found"));

    CloudKitchen kitchen = manager.getCloudKitchen();
    if (kitchen == null) {
        throw new CustomException("No Cloud Kitchen assigned to manager");
    }

    // Fetch available & active delivery persons for this kitchen
    List<DeliveryPerson> availableDPs = deliveryPersonRepo
            .findByCloudKitchenAndIsAvailableTrueAndIsActiveTrue(kitchen);

    // Map to DTO (only required fields)
    return availableDPs.stream()
            .map(dp -> new DeliveryPersonDTO(
                    dp.getDeliveryPersonId(),
                    dp.getName(),
                    dp.getEmail(),
                    dp.getPhoneNo(), // Only essential fields
                    null, // cloudKitchenId not needed here
                    null, // managerId not needed here
                    null  // otp not needed
            ))
            .toList();
}



    @Override
    public String openAndCloseCloudKitchen() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Manager manager = managerRepo.findByManagerEmail(email)
                .orElseThrow(() -> new CustomException("Manager not found"));

        CloudKitchen kitchen = manager.getCloudKitchen();
        if (kitchen == null) {
            throw new CustomException("No Cloud Kitchen assigned to this manager");
        }

        // Toggle status
        boolean newStatus = !Boolean.TRUE.equals(kitchen.getIsOpen());
        kitchen.setIsOpen(newStatus);

        // Save change
        managerRepo.save(manager); // kitchen is managed entity (via relationship)

        return newStatus
                ? "Cloud Kitchen is now OPEN for orders"
                : "Cloud Kitchen is now CLOSED for orders";
    }
    
    //New get all manager side chatboat api

    @Override
    public List<ManagerIssueDto> getAllIssuesForManager() {

        // ✅ Logged-in manager ka email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Manager manager = managerRepo.findByManagerEmail(email)
                .orElseThrow(() -> new CustomException("Manager not found"));

        // ✅ Manager ka cloud kitchen
        CloudKitchen kitchen = manager.getCloudKitchen();
        if (kitchen == null) {
            throw new CustomException("Manager has no cloud kitchen assigned");
        }

        String kitchenId = kitchen.getCloudKitchenId();

        // ✅ Fetch only issues of this cloudKitchen
        List<IssueReport> issues = issueRepo.findByCloudKitchenId(kitchenId);

        return issues.stream().map(issue -> {
            ManagerIssueDto dto = new ManagerIssueDto();

            dto.setUserName(issue.getUserName());
            dto.setPhoneNumber(issue.getPhoneNumber());
            dto.setAddress(issue.getAddress());
            dto.setOrderId(issue.getOrderId());
            dto.setIssueType(issue.getIssueType().name());
            dto.setPhotoUrl(issue.getPhotoUrl());

            return dto;
        }).toList();
    }



}
