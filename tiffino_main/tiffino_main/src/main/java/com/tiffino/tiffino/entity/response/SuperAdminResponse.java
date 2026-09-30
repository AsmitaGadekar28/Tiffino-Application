package com.tiffino.tiffino.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SuperAdminResponse {
    private String adminName;
    private String email;
    private String role;
}


