package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuperAdminRepo extends JpaRepository<SuperAdmin, Long> {
    Optional<SuperAdmin> findByEmail(String email);


}
