package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.CloudKitchen;
import com.tiffino.tiffino.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findByCloudKitchen(CloudKitchen kitchen);
}
