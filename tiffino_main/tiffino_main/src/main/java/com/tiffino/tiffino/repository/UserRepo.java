package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByOtp(String otp);
    boolean existsByEmail(String email);
    boolean existsByPhoneNo(String phoneNo);
}
