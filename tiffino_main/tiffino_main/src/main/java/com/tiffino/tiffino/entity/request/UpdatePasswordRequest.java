package com.tiffino.tiffino.entity.request;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String otp;
    private String newPassword;
}

