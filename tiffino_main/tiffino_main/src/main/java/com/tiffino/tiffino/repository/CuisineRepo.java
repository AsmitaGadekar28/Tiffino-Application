package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuisineRepo extends JpaRepository<Cuisine, Long> {
    // Single cuisine search ignoring case
    Optional<Cuisine> findByNameIgnoreCase(String name);

    // Multiple cuisines search ignoring case
    List<Cuisine> findByNameInIgnoreCase(List<String> names);

}
