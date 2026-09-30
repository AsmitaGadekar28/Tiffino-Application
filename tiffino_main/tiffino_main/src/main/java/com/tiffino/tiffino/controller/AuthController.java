package com.tiffino.tiffino.controller;

import com.tiffino.tiffino.entity.request.LoginRequest;
import com.tiffino.tiffino.entity.request.RegisterRequest;
import com.tiffino.tiffino.entity.response.LoginResponse;
import com.tiffino.tiffino.entity.response.MealWithCuisineResponse;
import com.tiffino.tiffino.service.AuthService;
import com.tiffino.tiffino.service.TokenBlacklistService;
import com.tiffino.tiffino.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private UserService userService;

@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
    LoginResponse response = authService.login(req.getEmailOrId(), req.getPassword());
    // Track issued token per user
    tokenBlacklistService.addTokenForUser(response.getToken(), response.getEmail());
    return ResponseEntity.ok(response);
}

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = authService.getUsernameFromToken(token);
            tokenBlacklistService.blacklistToken(token, username); // blacklist current token
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("No token found in request");
    }

    // Register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        String msg = userService.register(req);
        return ResponseEntity.ok(msg);
    }

    // Protected endpoint example
    @GetMapping("/getAllAvailableMealsWithCuisine")
    public ResponseEntity<List<MealWithCuisineResponse>> getAllAvailableMeals(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = authHeader.substring(7);

        // Check if token is blacklisted
        if (tokenBlacklistService.isBlacklisted(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<MealWithCuisineResponse> meals = userService.getAllAvailableMealsWithCuisine(token);
        return ResponseEntity.ok(meals);
    }


    @GetMapping("/getProfile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
            }

            String token = authHeader.substring(7);

            // check if token blacklisted
            if (tokenBlacklistService.isBlacklisted(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired or invalid");
            }

            // extract username/email from token
            String email = authService.getUsernameFromToken(token);

            // 1️⃣ check SuperAdmin
            var superAdminOpt = authService.getSuperAdminRepo().findByEmail(email);
            if (superAdminOpt.isPresent()) {
                return ResponseEntity.ok(superAdminOpt.get());
            }

            // 2️⃣ check Manager
            var managerOpt = authService.getManagerRepo().findByManagerEmail(email);
            if (managerOpt.isPresent()) {
                return ResponseEntity.ok(managerOpt.get());
            }

            // 3️⃣ check DeliveryPerson
            var dpOpt = authService.getDeliveryPersonRepo().findByEmail(email);
            if (dpOpt.isPresent()) {
                return ResponseEntity.ok(dpOpt.get());
            }

            // 4️⃣ check User
            var userOpt = authService.getUserRepo().findByEmail(email);
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(userOpt.get());
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No profile found for token");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}
