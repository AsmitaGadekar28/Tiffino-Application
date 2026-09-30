package com.tiffino.tiffino.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tiffino.tiffino.dto.*;
import com.tiffino.tiffino.entity.*;
import com.tiffino.tiffino.entity.request.CloudKitchenRequest;
import com.tiffino.tiffino.entity.request.DeliveryPersonRequest;
import com.tiffino.tiffino.entity.request.ManagerRequest;
import com.tiffino.tiffino.entity.request.SuperAdminRequest;
import com.tiffino.tiffino.entity.response.CloudKitchenReviewResponse;
import com.tiffino.tiffino.entity.response.SubscriberUserResponse;
import com.tiffino.tiffino.entity.response.SuperAdminResponse;
import com.tiffino.tiffino.exception.CustomException;
import com.tiffino.tiffino.repository.*;
import com.tiffino.tiffino.util.OtpUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class SuperAdminService implements ISuperadminService {

    @Autowired
    private SuperAdminRepo superAdminRepo;

    @Autowired
    private CloudKitchenRepo cloudKitchenRepo;

    @Autowired
    private ManagerRepo managerRepo;

    @Autowired
    private DeliveryPersonRepo deliveryPersonRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    private EmailValidationService emailValidationService;

    public SuperAdminService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }


    // Auto-create SuperAdmin on startup
 /**   @PostConstruct
    public void createSuperAdminIfNotExist() {
        Optional<SuperAdmin> existing = superAdminRepo.findByEmail("admin@gmail.com");
        if (existing.isEmpty()) {
            SuperAdmin superAdmin = new SuperAdmin();
            superAdmin.setAdminName("Super Admin");
            superAdmin.setEmail("admin@gmail.com");
            superAdmin.setPassword(passwordEncoder.encode("admin"));
            superAdminRepo.save(superAdmin);
        }
    }**/
 // Auto-create SuperAdmin on startup
 @PostConstruct
 public void createSuperAdminIfNotExist() {
     Optional<SuperAdmin> existing = superAdminRepo.findByEmail("admin@gmail.com");
     if (existing.isEmpty()) {
         SuperAdmin superAdmin = new SuperAdmin();
         superAdmin.setAdminName("Super Admin");
         superAdmin.setEmail("admin@gmail.com");
         superAdmin.setPassword(passwordEncoder.encode("admin"));
         superAdminRepo.save(superAdmin);
     }
 }
/**
    // Update SuperAdmin and blacklist old tokens
    // Update SuperAdmin info
    public SuperAdminResponse updateSuperAdmin(SuperAdminRequest request) {
        // Fetch SuperAdmin by email
        SuperAdmin superAdmin = superAdminRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("SuperAdmin not found"));

        // Update name
        superAdmin.setAdminName(request.getAdminName());

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
            superAdmin.setLastPasswordChange(new Date());

            // ✅ Blacklist all existing tokens for this SuperAdmin
            tokenBlacklistService.blacklistTokensOfUser(superAdmin.getEmail());
        }

        superAdminRepo.save(superAdmin);

        return new SuperAdminResponse(
                superAdmin.getAdminName(),
                superAdmin.getEmail(),
                superAdmin.getRole().name()
        );
    }**/
public SuperAdminResponse updateSuperAdmin(SuperAdminRequest request) {
    // ✅ Find SuperAdmin by ID (preferred) or fallback to the first one
    SuperAdmin superAdmin = null;

    if (request.getId() != null) {
        superAdmin = superAdminRepo.findById(request.getId())
                .orElseThrow(() -> new CustomException("SuperAdmin not found with ID: " + request.getId()));
    } else {
        superAdmin = superAdminRepo.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new CustomException("SuperAdmin not found"));
    }

    // ✅ Update name
    if (request.getAdminName() != null && !request.getAdminName().isEmpty()) {
        superAdmin.setAdminName(request.getAdminName());
    }

    // ✅ Update email (if new and not same)
    if (request.getEmail() != null && !request.getEmail().isEmpty()
            && !request.getEmail().equals(superAdmin.getEmail())) {

        if (superAdminRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Email already in use by another account");
        }

        // ⚠️ Blacklist old tokens before changing email
        tokenBlacklistService.blacklistTokensOfUser(superAdmin.getEmail());

        superAdmin.setEmail(request.getEmail());
    }

    // ✅ Update password
    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
        superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        superAdmin.setLastPasswordChange(new Date());

        // ⚠️ Blacklist tokens again after password change
        tokenBlacklistService.blacklistTokensOfUser(superAdmin.getEmail());
    }

    // ✅ Save updated SuperAdmin
    superAdminRepo.save(superAdmin);

    return new SuperAdminResponse(
            superAdmin.getAdminName(),
            superAdmin.getEmail(),
            superAdmin.getRole().name()
    );
}


    //  Update SuperAdmin
    /**public SuperAdminResponse updateSuperAdmin(SuperAdminRequest request) {
        SuperAdmin superAdmin = superAdminRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("SuperAdmin not found"));

        superAdmin.setAdminName(request.getAdminName());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            superAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        superAdminRepo.save(superAdmin);

        return new SuperAdminResponse(superAdmin.getAdminName(), superAdmin.getEmail(), superAdmin.getRole().name());
    }
**/



    // Auto-generate CloudKitchenId
    private String generateCloudKitchenId(String city, String division) {
        String prefix = city.substring(0,3).toUpperCase() + division.substring(0,3).toUpperCase();
        long count = cloudKitchenRepo.countByCityAndDivision(city, division) + 1;
        return prefix + String.format("%03d", count);
    }

    // Auto-generate ManagerId
    private String generateManagerId(String city) {
        String prefix = "MAN" + city.substring(0,3).toUpperCase();
        long count = managerRepo.countByCity(city) + 1;
        return prefix + String.format("%03d", count);
    }

    // Save CloudKitchen
    public CloudKitchen saveCloudKitchen(CloudKitchenRequest request) {
        String cloudKitchenId = generateCloudKitchenId(request.getCity(), request.getDivision());

        CloudKitchen kitchen = new CloudKitchen();
        kitchen.setCloudKitchenId(cloudKitchenId);
        kitchen.setCity(request.getCity());
        kitchen.setState(request.getState());
        kitchen.setDivision(request.getDivision());
        kitchen.setAddress(request.getAddress());
        kitchen.setPinCode(request.getPinCode());

        return cloudKitchenRepo.save(kitchen);
    }
/**
    // Save Manager with OTP
    public String saveManagerWithOtp(ManagerRequest request) {
        //  CloudKitchen fetch
        CloudKitchen cloudKitchen = cloudKitchenRepo.findById(request.getCloudKitchenId())
                .orElseThrow(() -> new CustomException("Cloud Kitchen not found"));

        //  ManagerId generate
        String managerId = generateManagerId(request.getCity());

        // OTP generate
        String otp = OtpUtil.generateOtp();

        // Manager entity set
        Manager manager = new Manager();
        manager.setManagerId(managerId);
        manager.setManagerName(request.getManagerName());
        manager.setManagerEmail(request.getManagerEmail());


        manager.setOtp(otp);


        manager.setPassword(null);

        manager.setCity(request.getCity());
       manager.setDob(request.getDob());
        manager.setPhoneNo(request.getPhoneNo());
        manager.setCurrentAddress(request.getCurrentAddress());
        manager.setPermeantAddress(request.getPermeantAddress());
        manager.setAdharCard(request.getAdharCard());
        manager.setPanCard(request.getPanCard());
        manager.setPhoto(request.getPhoto());

        //  Assign CloudKitchen
        manager.setCloudKitchen(cloudKitchen);

        managerRepo.save(manager);

        // Email body
        String subject = "Welcome! Your Manager Account is Created";
        String body = "Hello " + manager.getManagerName() + ",\n\n" +
                "Now you are the Manager of " + cloudKitchen.getCloudKitchenId() + " (" + cloudKitchen.getCity() + ").\n" +
                "Your Manager ID is: " + managerId + "\n" +
                "Your One-Time Password (OTP) is: " + otp + "\n\n" +
                "Please use this OTP to set your new password.";

        emailService.sendEmail(manager.getManagerEmail(), subject, body);

        return "✅ Manager saved successfully! OTP sent to email/console.";
    }**/

@Autowired
private Cloudinary cloudinary;

    private String uploadFile(MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                var uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                return uploadResult.get("secure_url").toString(); // Cloudinary URL
            }
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
        return null;
    }

    public String saveManagerWithOtp(ManagerRequest request) {

        // ✅ Email deliverability check
        if (!emailValidationService.isEmailDeliverable(request.getManagerEmail())) {
            throw new CustomException("Invalid Or Not Deliverable email");
        }
        //  CloudKitchen fetch
        CloudKitchen cloudKitchen = cloudKitchenRepo.findById(request.getCloudKitchenId())
                .orElseThrow(() -> new CustomException("Cloud Kitchen not found"));

        //  ManagerId generate
        String managerId = generateManagerId(request.getCity());

        // OTP generate
        String otp = OtpUtil.generateOtp();

        // File uploads to Cloudinary
        String adharUrl = uploadFile(request.getAdharCard());
        String panUrl = uploadFile(request.getPanCard());
        String photoUrl = uploadFile(request.getPhoto());

        // Manager entity set
        Manager manager = new Manager();
        manager.setManagerId(managerId);
        manager.setManagerName(request.getManagerName());
        manager.setManagerEmail(request.getManagerEmail());
        manager.setOtp(otp);
        manager.setPassword(null);
        manager.setCity(request.getCity());
        manager.setDob(request.getDob());
        manager.setPhoneNo(request.getPhoneNo());
        manager.setCurrentAddress(request.getCurrentAddress());
        manager.setPermeantAddress(request.getPermeantAddress());

        // ✅ Save Cloudinary URLs in DB
        manager.setAdharCard(adharUrl);
        manager.setPanCard(panUrl);
        manager.setPhoto(photoUrl);

        //  Assign CloudKitchen
        manager.setCloudKitchen(cloudKitchen);

        managerRepo.save(manager);

        // Email body
        String subject = "Welcome! Your Manager Account is Created";
        String body = "Hello " + manager.getManagerName() + ",\n\n" +
                "Now you are the Manager of " + cloudKitchen.getCloudKitchenId() + " (" + cloudKitchen.getCity() + ").\n" +
                "Your Manager ID is: " + managerId + "\n" +
                "Your One-Time Password (OTP) is: " + otp + "\n\n" +
                "Please use this OTP to set your new password.";

        emailService.sendEmail(manager.getManagerEmail(), subject, body);

        return "✅ Manager saved successfully with uploaded documents! OTP sent to email.";
    }

    // Get all managers with CloudKitchen (DTO)
  /**  @Override
    public List<ManagerCloudKitchenDTO> getAllManagersWithCloudKitchen() {
        //  Use DISTINCT query from repository instead of plain findByCloudKitchenIsNotNull()
        List<Manager> managers = managerRepo.findAllManagersWithCloudKitchen();

        return managers.stream().map(manager ->
                new ManagerCloudKitchenDTO(
                        manager.getManagerId(),
                        manager.getManagerName(),
                        manager.getManagerEmail(),
                        manager.getCloudKitchen().getCloudKitchenId(),
                        manager.getCloudKitchen().getCity()
                )
        ).collect(Collectors.toList());
    }**/

  @Override
  public List<ManagerCloudKitchenDTO> getAllManagersWithCloudKitchen() {
      List<Manager> managers = managerRepo.findAllManagersWithCloudKitchen();

      // ✅ Exclude managers linked to deleted cloud kitchens
      return managers.stream()
              .filter(manager -> manager.getCloudKitchen() != null &&
                      !Boolean.TRUE.equals(manager.getCloudKitchen().getIsDeleted()))
              .map(manager -> new ManagerCloudKitchenDTO(
                      manager.getManagerId(),
                      manager.getManagerName(),
                      manager.getManagerEmail(),
                      manager.getCloudKitchen().getCloudKitchenId(),
                      manager.getCloudKitchen().getCity()
              ))
              .collect(Collectors.toList());
  }

    //delete manager by id
    @Override
    public String deleteManagerById(String managerId) {
        Manager manager = managerRepo.findById(managerId)
                .orElseThrow(() -> new CustomException("Manager not found with id: " + managerId));

        manager.setIsDeleted(true);   // soft delete
        manager.setIsActive(false);   // inactive bhi kar do
        managerRepo.save(manager);

        return "✅ Manager soft deleted successfully with ID: " + managerId;
    }

    //delete cloud kitchen by id
    @Override
    public String deleteCloudKitchenById(String cloudKitchenId) {
        CloudKitchen cloudKitchen = cloudKitchenRepo.findById(cloudKitchenId)
                .orElseThrow(() -> new CustomException("CloudKitchen not found with id: " + cloudKitchenId));

        // ✅ Soft delete
        cloudKitchen.setIsDeleted(true);
        cloudKitchen.setIsActive(false);
        cloudKitchenRepo.save(cloudKitchen);

        return "✅ CloudKitchen soft deleted successfully with ID: " + cloudKitchenId;
    }

    //save delivery person
    public DeliveryPersonDTO saveDeliveryPerson(DeliveryPersonRequest request, String cloudKitchenId) {

        // ✅ Email deliverability check
        if (!emailValidationService.isEmailDeliverable(request.getEmail())) {
            throw new CustomException("Invalid Or Not Deliverable email");
        }
        // CloudKitchen fetch
        CloudKitchen cloudKitchen = cloudKitchenRepo.findById(cloudKitchenId)
                .orElseThrow(() -> new CustomException("CloudKitchen not found"));

        // Manager check
        Manager manager = cloudKitchen.getManager();
        if (manager == null) throw new CustomException("No Manager assigned to CloudKitchen");

        // DeliveryPerson entity set
        DeliveryPerson dp = new DeliveryPerson();
        dp.setName(request.getName());
        dp.setEmail(request.getEmail());
        dp.setPassword(passwordEncoder.encode(request.getPassword()));
        dp.setPhoneNo(request.getPhoneNo());

        dp.setCloudKitchen(cloudKitchen);
        dp.setManager(manager);

        // OTP generate
        String otp = OtpUtil.generateOtp();
        dp.setOtp(otp);

        // Save
        DeliveryPerson saved = deliveryPersonRepo.save(dp);

        // Email notification
        String subject = "Welcome! Your Delivery Person Account is Created";
        String body = "Hello " + saved.getName() + ",\n\n" +
                "Cloud Kitchen ID: " + cloudKitchen.getCloudKitchenId() + "\n" +
                "Manager ID: " + manager.getManagerId() + "\n\n" +
                "Your OTP: " + otp + "\n\n" +
                "Please contact your manager for instructions.";
        emailService.sendEmail(saved.getEmail(), subject, body);

        // Return DTO
        return new DeliveryPersonDTO(
                saved.getDeliveryPersonId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPhoneNo(),
                cloudKitchen.getCloudKitchenId(),
                manager.getManagerId(),
                otp
        );
    }


    public List<SubscriberUserResponse> getAllSubscriberUsers() {
        List<Subscription> activeSubscriptions = subscriptionRepository.findByActiveTrue();

        return activeSubscriptions.stream()
                .map(sub -> new SubscriberUserResponse(
                        sub.getUser().getUserName(),
                        sub.getFinalPrice(),
                        sub.getDurationType()
                ))
                .collect(Collectors.toList());
    }

    // ✅ Get all cloud kitchens with their reviews & manager info
    public List<CloudKitchenReviewResponse> getAllCloudKitchensAndReviews() {
        List<CloudKitchen> kitchens = cloudKitchenRepo.findAll();

        return kitchens.stream().map(kitchen -> {
            CloudKitchenReviewResponse response = new CloudKitchenReviewResponse();

            response.setCloudKitchenId(kitchen.getCloudKitchenId());

            String managerId = (kitchen.getManager() != null) ? kitchen.getManager().getManagerId() : null;
            response.setManagerId(managerId);


            response.setState(kitchen.getState());
            response.setCity(kitchen.getCity());
            response.setDivision(kitchen.getDivision());

            // safe fetch reviews
            List<Review> reviews = Optional.ofNullable(reviewRepo.findByCloudKitchen(kitchen))
                    .orElse(Collections.emptyList());

            List<ReviewResponse> reviewResponses = reviews.stream()
                    .map(r -> new ReviewResponse(r.getCloudKitchenReview(), r.getRating()))
                    .collect(Collectors.toList());

            response.setReviews(reviewResponses);

            return response;
        }).collect(Collectors.toList());
    }


    public List<SearchFilterResponseDTO> searchFilter(SearchFilterRequestDTO request) {

        // ✅ Check if all fields are empty or null
        boolean noFiltersProvided = (request.getStateNames() == null || request.getStateNames().isEmpty()) &&
                (request.getCities() == null || request.getCities().isEmpty()) &&
                (request.getDivisions() == null || request.getDivisions().isEmpty());

        List<CloudKitchen> kitchens;

        // ✅ If no filters are provided, return all CloudKitchens
        if (noFiltersProvided) {
            kitchens = cloudKitchenRepo.findAll();
        } else {
            kitchens = cloudKitchenRepo.findByFilters(
                    request.getStateNames(),
                    request.getCities(),
                    request.getDivisions()
            );
        }

        // ✅ Map CloudKitchen and Manager details to Response DTO
        return kitchens.stream()
                .map(ck -> {
                    Manager m = ck.getManager();
                    return SearchFilterResponseDTO.builder()
                            .cloudKitchenId(ck.getCloudKitchenId())
                            .city(ck.getCity())
                            .division(ck.getDivision())
                            .isActive(ck.getIsActive())
                            .isDeleted(ck.getIsDeleted())
                            .createdAt(ck.getCreatedAt())
                            .managerId(m != null ? m.getManagerId() : null)
                            .managerName(m != null ? m.getManagerName() : null)
                            .managerIsActive(m != null ? m.getIsActive() : null)
                            .managerIsDeleted(m != null ? m.getIsDeleted() : null)
                            .managerCreatedAt(m != null ? m.getCreatedAt() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }


}



