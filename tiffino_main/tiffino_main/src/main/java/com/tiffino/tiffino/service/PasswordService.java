package com.tiffino.tiffino.service;

import com.tiffino.tiffino.entity.DeliveryPerson;
import com.tiffino.tiffino.entity.Manager;
import com.tiffino.tiffino.entity.User;
import com.tiffino.tiffino.exception.CustomException;
import com.tiffino.tiffino.repository.DeliveryPersonRepo;
import com.tiffino.tiffino.repository.ManagerRepo;
import com.tiffino.tiffino.repository.UserRepo;
import com.tiffino.tiffino.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepo userRepo;
    private final DeliveryPersonRepo deliveryPersonRepo;
    private final ManagerRepo managerRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 🔹 Forgot password - send OTP (can use email OR managerId)
    public String forgotPassword(String identifier) {
        Object entity = null;
        String role = null;
        String email = null;

        // Check Manager by email
        entity = managerRepo.findByManagerEmail(identifier).orElse(null);
        if (entity != null) {
            role = "MANAGER";
            email = ((Manager) entity).getManagerEmail();
        }

        // Check Manager by ID (if not found by email)
        if (entity == null) {
            entity = managerRepo.findByManagerId(identifier).orElse(null);
            if (entity != null) {
                role = "MANAGER";
                email = ((Manager) entity).getManagerEmail();
            }
        }

        // Check User
        if (entity == null) {
            entity = userRepo.findByEmail(identifier).orElse(null);
            if (entity != null) {
                role = "USER";
                email = ((User) entity).getEmail();
            }
        }

        // Check Delivery Person
        if (entity == null) {
            entity = deliveryPersonRepo.findByEmail(identifier).orElse(null);
            if (entity != null) {
                role = "DELIVERY";
                email = ((DeliveryPerson) entity).getEmail();
            }
        }

        if (entity == null) throw new CustomException("No account found with given details: " + identifier);

        // Generate OTP
        String otp = OtpUtil.generateOtp();

        // Save OTP
        if (role.equals("MANAGER")) {
            ((Manager) entity).setOtp(otp);
            managerRepo.save((Manager) entity);
        } else if (role.equals("USER")) {
            ((User) entity).setOtp(otp);
            userRepo.save((User) entity);
        } else {
            ((DeliveryPerson) entity).setOtp(otp);
            deliveryPersonRepo.save((DeliveryPerson) entity);
        }

        // Send email
        String subject = role + " Password Reset OTP";
        String body = "Hello, your OTP to reset password is: " + otp;
        emailService.sendEmail(email, subject, body);

        return "OTP sent to registered email!";
    }

    // 🔹 Change password using only OTP (email optional)
    public String changePassword(String emailOrOtp, String otp, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new CustomException("New password and confirm password do not match!");
        }

        Object entity = null;
        String role = null;

        // If emailOrOtp is OTP itself (when email not given)
        if (emailOrOtp == null || emailOrOtp.isEmpty()) {
            entity = managerRepo.findByOtp(otp).orElse(null);
            if (entity != null) role = "MANAGER";
            if (entity == null) {
                entity = userRepo.findByOtp(otp).orElse(null);
                if (entity != null) role = "USER";
            }
            if (entity == null) {
                entity = deliveryPersonRepo.findByOtp(otp).orElse(null);
                if (entity != null) role = "DELIVERY";
            }
        } else {
            // Try with email (for backward compatibility)
            entity = managerRepo.findByManagerEmail(emailOrOtp).orElse(null);
            if (entity != null) role = "MANAGER";
            if (entity == null) {
                entity = userRepo.findByEmail(emailOrOtp).orElse(null);
                if (entity != null) role = "USER";
            }
            if (entity == null) {
                entity = deliveryPersonRepo.findByEmail(emailOrOtp).orElse(null);
                if (entity != null) role = "DELIVERY";
            }
        }

        if (entity == null) throw new CustomException("Invalid email or OTP!");

        // Validate OTP
        String savedOtp = null;
        if (role.equals("MANAGER")) savedOtp = ((Manager) entity).getOtp();
        else if (role.equals("USER")) savedOtp = ((User) entity).getOtp();
        else savedOtp = ((DeliveryPerson) entity).getOtp();

        if (savedOtp == null || !savedOtp.equals(otp.trim())) {
            throw new CustomException("Invalid OTP!");
        }

        // Update password & clear OTP
        if (role.equals("MANAGER")) {
            ((Manager) entity).setPassword(passwordEncoder.encode(newPassword));
            ((Manager) entity).setOtp(null);
            managerRepo.save((Manager) entity);
        } else if (role.equals("USER")) {
            ((User) entity).setPassword(passwordEncoder.encode(newPassword));
            ((User) entity).setOtp(null);
            userRepo.save((User) entity);
        } else {
            ((DeliveryPerson) entity).setPassword(passwordEncoder.encode(newPassword));
            ((DeliveryPerson) entity).setOtp(null);
            deliveryPersonRepo.save((DeliveryPerson) entity);
        }

        return "Password changed successfully!";
    }

    // 🔹 Update password using only OTP (no email)
    public String updatePassword(String otp, String newPassword) {
        Object entity = null;
        String role = null;

        entity = managerRepo.findByOtp(otp).orElse(null);
        if (entity != null) role = "MANAGER";

        if (entity == null) {
            entity = userRepo.findByOtp(otp).orElse(null);
            if (entity != null) role = "USER";
        }

        if (entity == null) {
            entity = deliveryPersonRepo.findByOtp(otp).orElse(null);
            if (entity != null) role = "DELIVERY";
        }

        if (entity == null) throw new CustomException("Invalid OTP!");

        if (role.equals("MANAGER")) {
            ((Manager) entity).setPassword(passwordEncoder.encode(newPassword));
            ((Manager) entity).setOtp(null);
            managerRepo.save((Manager) entity);
        } else if (role.equals("USER")) {
            ((User) entity).setPassword(passwordEncoder.encode(newPassword));
            ((User) entity).setOtp(null);
            userRepo.save((User) entity);
        } else {
            ((DeliveryPerson) entity).setPassword(passwordEncoder.encode(newPassword));
            ((DeliveryPerson) entity).setOtp(null);
            deliveryPersonRepo.save((DeliveryPerson) entity);
        }

        return "Password updated successfully!";
    }
}
