package com.tiffino.tiffino.controller;

import com.tiffino.tiffino.entity.request.ChangePasswordRequest;
import com.tiffino.tiffino.entity.request.ForgotPasswordRequest;
import com.tiffino.tiffino.entity.request.UpdatePasswordRequest;
import com.tiffino.tiffino.service.PasswordService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    // Forgot Password - send OTP
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String message = passwordService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(message);
    }

    //  Change Password - OTP + newPassword + confirmPassword
    @PostMapping("/change")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        String message = passwordService.changePassword(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );
        return ResponseEntity.ok(message);
    }

    //  Update Password - OTP + newPassword
    @PostMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest request) {
        String message = passwordService.updatePassword(
                request.getOtp(),
                request.getNewPassword()
        );
        return ResponseEntity.ok(message);
    }
}
