package com.tiffino.tiffino.service;

import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.entity.Manager;
import com.tiffino.tiffino.entity.SuperAdmin;
import com.tiffino.tiffino.entity.User;
import com.tiffino.tiffino.entity.response.LoginResponse;
import com.tiffino.tiffino.exception.CustomException;
import com.tiffino.tiffino.repository.DeliveryPersonRepo;
import com.tiffino.tiffino.repository.ManagerRepo;
import com.tiffino.tiffino.repository.SuperAdminRepo;
import com.tiffino.tiffino.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SuperAdminRepo superAdminRepo;
    private final ManagerRepo managerRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final DeliveryPersonRepo deliveryPersonRepo;
    public SuperAdminRepo getSuperAdminRepo() {
        return superAdminRepo;
    }

    public ManagerRepo getManagerRepo() {
        return managerRepo;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public DeliveryPersonRepo getDeliveryPersonRepo() {
        return deliveryPersonRepo;
    }




    // Login method
    public LoginResponse login(String emailOrId, String password) {



        // SuperAdmin
        if (emailOrId.contains("@")) {
            var superAdminOpt = superAdminRepo.findByEmail(emailOrId);
            if (superAdminOpt.isPresent()) {
                SuperAdmin admin = superAdminOpt.get();
                if (!passwordEncoder.matches(password, admin.getPassword())) {
                    throw new CustomException("Invalid password for SuperAdmin");
                }
                String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole().name());
                return new LoginResponse(admin.getEmail(), token, admin.getRole().name(), null);
            }
        }

        // Manager
        Manager manager;
        if (emailOrId.contains("@")) {
            manager = managerRepo.findByManagerEmail(emailOrId).orElse(null);
        } else {
            manager = managerRepo.findByManagerId(emailOrId).orElse(null);
        }

        if (manager != null) {
            if (!passwordEncoder.matches(password, manager.getPassword())) {
                throw new CustomException("Invalid password for Manager");
            }
            String token = jwtUtil.generateToken(manager.getManagerEmail(), "MANAGER");
            return new LoginResponse(manager.getManagerEmail(), token, "MANAGER", manager.getManagerId());
        }
        //  DeliveryPerson login
        if (emailOrId.contains("@")) {
            var dpOpt = deliveryPersonRepo.findByEmail(emailOrId);
            if (dpOpt.isPresent()) {
                var dp = dpOpt.get();

                if (dp.getPassword() == null) {
                    throw new CustomException("Password not set. Please reset using OTP.");
                }

                if (!passwordEncoder.matches(password, dp.getPassword())) {
                    throw new CustomException("Invalid password for Delivery Person");
                }

                String token = jwtUtil.generateToken(dp.getEmail(), "DELIVERY_PERSON");
                return new LoginResponse(dp.getEmail(), token, "DELIVERY_PERSON", null); // managerId not needed
            }
        }

        // User
        if (emailOrId.contains("@")) {
            User user = userRepo.findByEmail(emailOrId)
                    .orElseThrow(() -> new CustomException("User not found with email: " + emailOrId));
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new CustomException("Invalid password for User");
            }
            String token = jwtUtil.generateToken(user.getEmail(), "USER");
            return new LoginResponse(user.getEmail(), token, "USER", null);
        }

        throw new CustomException("Invalid login credentials");
    }

    // Extract username/email from JWT token
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }

}
