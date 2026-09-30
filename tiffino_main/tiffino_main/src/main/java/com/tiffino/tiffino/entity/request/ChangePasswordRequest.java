package com.tiffino.tiffino.entity.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}
