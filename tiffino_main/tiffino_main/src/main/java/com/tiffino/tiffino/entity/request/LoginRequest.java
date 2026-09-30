package com.tiffino.tiffino.entity.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String emailOrId;
    private String password;
}

