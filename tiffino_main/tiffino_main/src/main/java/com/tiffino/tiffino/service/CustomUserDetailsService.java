package com.tiffino.tiffino.service;

import com.tiffino.tiffino.entity.DeliveryPerson;
import com.tiffino.tiffino.entity.SuperAdmin;
import com.tiffino.tiffino.repository.DeliveryPersonRepo;
import com.tiffino.tiffino.repository.ManagerRepo;
import com.tiffino.tiffino.repository.SuperAdminRepo;
import com.tiffino.tiffino.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SuperAdminRepo superAdminRepo;
    private final ManagerRepo managerRepo;
    private final UserRepo userRepo;
    private final DeliveryPersonRepo deliveryPersonRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Try SuperAdmin
        var superAdminOpt = superAdminRepo.findByEmail(username);
        if (superAdminOpt.isPresent()) {
            SuperAdmin admin = superAdminOpt.get();
            String role = "ROLE_" + admin.getRole().name();
            return new org.springframework.security.core.userdetails.User(
                    admin.getEmail(),
                    admin.getPassword(),
                    List.of(new SimpleGrantedAuthority(role))
            );
        }

        // Try Manager
        var managerOpt = managerRepo.findByManagerEmail(username);
        if (managerOpt.isPresent()) {
            var manager = managerOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    manager.getManagerEmail(),
                    manager.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_MANAGER"))
            );
        }

        //  User
        var userOpt = userRepo.findByEmail(username);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER")) // ROLE_ prefix must
            );
        }

        // Delivery Person ✅
        var dpOpt = deliveryPersonRepo.findByEmail(username);
        if (dpOpt.isPresent()) {
            DeliveryPerson dp = dpOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    dp.getEmail(),
                    dp.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_DELIVERY_PERSON"))
            );
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    // ✅ Add this helper method here
    public List<SimpleGrantedAuthority> getAuthoritiesFromRole(String role) {
        return List.of(new SimpleGrantedAuthority(role));
    }

}

