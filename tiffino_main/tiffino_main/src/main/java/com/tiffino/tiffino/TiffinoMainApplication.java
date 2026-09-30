package com.tiffino.tiffino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tiffino.tiffino.entity.SuperAdmin;
import com.tiffino.tiffino.repository.SuperAdminRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TiffinoMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiffinoMainApplication.class, args);
        System.out.println("TiffinoMainApplication started.....");
    }
/**
    @Bean
    public CommandLineRunner init(SuperAdminRepo repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByEmail("admin@example.com").isEmpty()) {
                SuperAdmin admin = new SuperAdmin();
                admin.setAdminName("Admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(encoder.encode("admin123")); // bcrypt password
                repo.save(admin);
                System.out.println("Default SuperAdmin created: admin@example.com / admin123");
            }
        };
    }**/
}
