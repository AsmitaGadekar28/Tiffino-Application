package com.tiffino.tiffino.entity.request;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ManagerRequest {
    private String managerId;
    private String managerName;
    private String managerEmail;
    private String password;
    private String city;
    private String dob;
    private String phoneNo;
    private String currentAddress;
    private String permeantAddress;

    private MultipartFile adharCard;  // Aadhaar Card file
    private MultipartFile panCard;    // PAN Card file
    private MultipartFile photo;      // Photo file

    private String cloudKitchenId;
}

