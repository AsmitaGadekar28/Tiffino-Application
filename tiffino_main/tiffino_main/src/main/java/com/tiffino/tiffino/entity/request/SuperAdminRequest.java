package com.tiffino.tiffino.entity.request;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuperAdminRequest {
    private Long id;
    private String adminName;
    private String email;
    private String password; // Optional: update password
}
