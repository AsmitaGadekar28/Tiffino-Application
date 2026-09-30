package com.tiffino.tiffino.entity.request;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerPasswordRequest {
    private String managerId;
   private String otp;
    private String newPassword;
}

