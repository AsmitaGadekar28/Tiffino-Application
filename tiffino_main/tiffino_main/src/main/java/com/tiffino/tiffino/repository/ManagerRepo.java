package com.tiffino.tiffino.repository;
import com.tiffino.tiffino.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepo extends JpaRepository<Manager, String> {
    Optional<Manager> findByManagerEmail(String email);

    Optional<Manager> findByOtp(String otp);
    //Optional<Manager> findByOtp(String otp);
    long countByCity(String city);

  /**  @Query("SELECT DISTINCT m FROM Manager m JOIN FETCH m.cloudKitchen")
    List<Manager> findAllManagersWithCloudKitchen();**/

    @Query("SELECT m FROM Manager m JOIN FETCH m.cloudKitchen ck WHERE ck.isDeleted = false")
    List<Manager> findAllManagersWithCloudKitchen();


    Optional<Manager> findByManagerId(String managerId);
}

