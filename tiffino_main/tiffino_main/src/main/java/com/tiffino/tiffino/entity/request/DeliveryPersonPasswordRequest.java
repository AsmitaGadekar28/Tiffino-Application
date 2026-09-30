package com.tiffino.tiffino.entity.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryPersonPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
}
