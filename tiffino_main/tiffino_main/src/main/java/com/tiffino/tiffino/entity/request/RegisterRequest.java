package com.tiffino.tiffino.entity.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String userName;
    private String email;
    private String phoneNo;
    private String password;
}
