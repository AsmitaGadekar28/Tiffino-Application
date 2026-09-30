/**package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.CloudKitchen;
import com.tiffino.tiffino.entity.CloudKitchenMeal;
import com.tiffino.tiffino.entity.Meal;
import com.tiffino.tiffino.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CloudKitchenMealRepo extends JpaRepository<CloudKitchenMeal, Long> {

    // Manager-specific mapping
    Optional<CloudKitchenMeal> findByCloudKitchenAndMealAndManager(CloudKitchen cloudKitchen, Meal meal, Manager manager);

  //List<CloudKitchenMeal> findByAvailableTrueAndMealAvailableTrue();



   List<CloudKitchenMeal> findAllByAvailableTrue();
    List<CloudKitchenMeal> findByCloudKitchen_CloudKitchenId(String cloudKitchenId);

    Optional<CloudKitchenMeal> findByCloudKitchenAndMeal(CloudKitchen ck, Meal meal);

    @Query("SELECT ckm FROM CloudKitchenMeal ckm " +
            "WHERE ckm.available = true AND ckm.cloudKitchen.isDeleted = false AND ckm.cloudKitchen.isActive = true")
    List<CloudKitchenMeal> findAllAvailableFromActiveKitchens();

}
**/


package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.CloudKitchen;
import com.tiffino.tiffino.entity.CloudKitchenMeal;
import com.tiffino.tiffino.entity.Meal;
import com.tiffino.tiffino.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CloudKitchenMealRepo extends JpaRepository<CloudKitchenMeal, Long> {

    // Find by kitchen, meal, and manager (specific mapping)
    Optional<CloudKitchenMeal> findByCloudKitchenAndMealAndManager(CloudKitchen cloudKitchen, Meal meal, Manager manager);

    // Get all available meals from active kitchens
    @Query("SELECT ckm FROM CloudKitchenMeal ckm " +
            "WHERE ckm.available = true AND ckm.cloudKitchen.isDeleted = false AND ckm.cloudKitchen.isActive = true")
    List<CloudKitchenMeal> findAllAvailableFromActiveKitchens();

    // Find all available meals (deprecated, use above)
    List<CloudKitchenMeal> findAllByAvailableTrue();

    // Find meals by kitchen ID
    List<CloudKitchenMeal> findByCloudKitchen_CloudKitchenId(String cloudKitchenId);

    // Find by kitchen and meal
    Optional<CloudKitchenMeal> findByCloudKitchenAndMeal(CloudKitchen ck, Meal meal);

    // Fetch all meals for a specific state from active kitchens
    @Query("SELECT ckm FROM CloudKitchenMeal ckm " +
            "WHERE ckm.cloudKitchen.state = :state " +
            "AND ckm.cloudKitchen.isActive = true")
    List<CloudKitchenMeal> findAllByCloudKitchenState(@Param("state") String state);

    // ✅ Proper method signature returning Optional
    Optional<CloudKitchenMeal> findFirstByMeal_MealId(Long mealId);

    List<CloudKitchenMeal> findAllByMeal_MealId(Long mealId);

    Optional<CloudKitchenMeal> findByMeal_MealIdAndCloudKitchen(Long mealId, CloudKitchen cloudKitchen);

    Optional<CloudKitchenMeal> findByCloudKitchen_CloudKitchenIdAndMeal_MealId(String cloudKitchenId, Long mealId);

    //Fixed: Fetch meals by CloudKitchen state name
    List<CloudKitchenMeal> findAllByCloudKitchen_State(String state);

}
