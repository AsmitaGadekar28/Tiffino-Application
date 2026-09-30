package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.CloudKitchen;
import com.tiffino.tiffino.entity.DeliveryPerson;
import com.tiffino.tiffino.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPersonRepo extends JpaRepository<DeliveryPerson, Long> {

    Optional<DeliveryPerson> findByEmail(String email);
    Optional<DeliveryPerson> findByOtp(String otp);
    List<DeliveryPerson> findByCloudKitchenAndIsAvailableTrueAndIsActiveTrue(CloudKitchen cloudKitchen);


}

