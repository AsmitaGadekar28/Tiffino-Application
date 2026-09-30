package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.Subscription;
import com.tiffino.tiffino.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepo extends JpaRepository<Subscription, Long> {

    // 🔹 Get all subscriptions of a user
    List<Subscription> findByUser(User user);

    // 🔹 Find active subscriptions of a user
    List<Subscription> findByUserAndActiveTrue(User user);

    // 🔹 Find expired but still active subscriptions (for auto deactivation)
    List<Subscription> findByActiveTrueAndEndDateBefore(LocalDateTime dateTime);

    // 🔹 Optional: Find active subscriptions by type (Daily/Monthly)
    List<Subscription> findByUserAndActiveTrueAndDurationType(User user, String durationType);


    Optional<Subscription> findFirstByUserAndEndDateAfter(User user, LocalDateTime date);

    Optional<Subscription> findTopByUserOrderByEndDateDesc(User user);


}
