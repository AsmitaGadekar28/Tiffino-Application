package com.tiffino.tiffino.util;

import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.entity.User;
import com.tiffino.tiffino.repository.UserRepo;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    public UserUtil(JwtUtil jwtUtil, UserRepo userRepo) {
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    public User getUserFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);
        return userRepo.findByEmail(email).orElse(null);
    }
}
