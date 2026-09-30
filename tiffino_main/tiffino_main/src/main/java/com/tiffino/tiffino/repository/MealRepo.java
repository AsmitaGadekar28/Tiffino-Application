package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepo extends JpaRepository<Meal, Long> {

    List<Meal> findAllByAvailableTrue();

    List<Meal> findByCuisine_NameContainingIgnoreCase(String cuisineName);
}
