
/**
import com.tiffino.tiffino.entity.CloudKitchen;
import com.tiffino.tiffino.entity.CloudKitchenMeal;
import com.tiffino.tiffino.entity.Meal;
import com.tiffino.tiffino.repository.CloudKitchenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final CloudKitchenRepo cloudKitchenRepo;

    public List<Map<String, Object>> searchFilterForUser(List<String> cuisineNames, List<String> cloudKitchenNames) {
        List<Map<String, Object>> response = new ArrayList<>();

        final List<String> finalCuisineNames = (cuisineNames != null)
                ? cuisineNames.stream().map(String::trim).map(String::toLowerCase).collect(Collectors.toList())
                : new ArrayList<>();

        final List<String> finalCloudKitchenNames = (cloudKitchenNames != null)
                ? cloudKitchenNames.stream().map(String::trim).map(String::toLowerCase).collect(Collectors.toList())
                : new ArrayList<>();

        // Fetch only non-deleted kitchens
        List<CloudKitchen> kitchensToShow = cloudKitchenRepo.findAllByIsDeletedFalse().stream()
                .filter(ck -> finalCloudKitchenNames.isEmpty() ||
                        finalCloudKitchenNames.contains(ck.getCity().toLowerCase() + "_" + ck.getDivision().toLowerCase()))
                .collect(Collectors.toList());

        for (CloudKitchen ck : kitchensToShow) {
            List<CloudKitchenMeal> ckmList = ck.getCloudKitchenMeals();

            // Filter meals by cuisine if provided
            if (!finalCuisineNames.isEmpty()) {
                ckmList = ckmList.stream()
                        .filter(ckm -> {
                            Meal meal = ckm.getMeal();
                            return meal != null &&
                                    meal.getCuisine() != null &&
                                    finalCuisineNames.contains(meal.getCuisine().getName().toLowerCase());
                        })
                        .collect(Collectors.toList());
            }

            // Build kitchen map only if there is at least one available meal
            List<CloudKitchenMeal> availableMeals = ckmList.stream()
                    .filter(CloudKitchenMeal::isAvailable)
                    .toList();

            if (!availableMeals.isEmpty()) {
                response.add(buildKitchenMap(ck, availableMeals));
            }
        }

        return response;
    }

    private Map<String, Object> buildKitchenMap(CloudKitchen ck, List<CloudKitchenMeal> ckmList) {
        Map<String, Object> map = new HashMap<>();
        map.put("cloudKitchenId", ck.getCloudKitchenId());
        map.put("cloudKitchenName", ck.getCity() + "_" + ck.getDivision());

        List<Map<String, Object>> meals = new ArrayList<>();
        if (ckmList != null) {
            for (CloudKitchenMeal ckm : ckmList) {
                Meal meal = ckm.getMeal();
                if (meal == null || meal.getCuisine() == null) continue;

                Map<String, Object> mealMap = new HashMap<>();
                mealMap.put("mealId", meal.getMealId());
                mealMap.put("mealName", meal.getName());
                mealMap.put("mealNutritionInformation", meal.getNutritionalInformation());
                mealMap.put("mealPrice", meal.getPrice());
                mealMap.put("mealPhoto", meal.getPhotos());
                mealMap.put("cuisineName", meal.getCuisine().getName());
                meals.add(mealMap);
            }
        }

        map.put("meals", meals);
        return map;
    }
}
**/

package com.tiffino.tiffino.service;
import com.tiffino.tiffino.entity.CloudKitchen;
import com.tiffino.tiffino.entity.CloudKitchenMeal;
import com.tiffino.tiffino.entity.Meal;
import com.tiffino.tiffino.repository.CloudKitchenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final CloudKitchenRepo cloudKitchenRepo;
     
    public List<Map<String, Object>> searchFilterForUserByState(List<String> stateNames, List<String> cloudKitchenNames) {
        List<Map<String, Object>> response = new ArrayList<>();

        final List<String> finalStateNames = (stateNames != null)
                ? stateNames.stream().map(String::trim).map(String::toLowerCase).collect(Collectors.toList())
                : new ArrayList<>();

        final List<String> finalCloudKitchenNames = (cloudKitchenNames != null)
                ? cloudKitchenNames.stream().map(String::trim).map(String::toLowerCase).collect(Collectors.toList())
                : new ArrayList<>();

        // Fetch only non-deleted kitchens
        List<CloudKitchen> kitchensToShow = cloudKitchenRepo.findAllByIsDeletedFalse().stream()
                .filter(ck -> finalCloudKitchenNames.isEmpty() ||
                        finalCloudKitchenNames.contains(ck.getCity().toLowerCase() + "_" + ck.getDivision().toLowerCase()))
                .collect(Collectors.toList());

        for (CloudKitchen ck : kitchensToShow) {
            List<CloudKitchenMeal> ckmList = ck.getCloudKitchenMeals();

            // Filter meals by stateName if provided
            if (!finalStateNames.isEmpty()) {
                ckmList = ckmList.stream()
                        .filter(ckm -> {
                            Meal meal = ckm.getMeal();
                            return meal != null &&
                                    meal.getCuisine() != null &&
                                    meal.getCuisine().getStateName() != null &&
                                    finalStateNames.contains(meal.getCuisine().getStateName().toLowerCase());
                        })
                        .collect(Collectors.toList());
            }

            // Build kitchen map only if there is at least one available meal
            List<CloudKitchenMeal> availableMeals = ckmList.stream()
                    .filter(CloudKitchenMeal::isAvailable)
                    .toList();

            if (!availableMeals.isEmpty()) {
                response.add(buildKitchenMap(ck, availableMeals));
            }
        }

        return response;
    }

    private Map<String, Object> buildKitchenMap(CloudKitchen ck, List<CloudKitchenMeal> ckmList) {
        Map<String, Object> map = new HashMap<>();
        map.put("cloudKitchenId", ck.getCloudKitchenId());
        map.put("cloudKitchenName", ck.getCity() + "_" + ck.getDivision());

        List<Map<String, Object>> meals = new ArrayList<>();
        if (ckmList != null) {
            for (CloudKitchenMeal ckm : ckmList) {
                Meal meal = ckm.getMeal();
                if (meal == null || meal.getCuisine() == null) continue;

                Map<String, Object> mealMap = new HashMap<>();
                mealMap.put("mealId", meal.getMealId());
                mealMap.put("mealName", meal.getName());
                mealMap.put("mealNutritionInformation", meal.getNutritionalInformation());
                mealMap.put("mealPrice", meal.getPrice());
                mealMap.put("mealPhoto", meal.getPhotos());
                mealMap.put("stateName", meal.getCuisine().getStateName());
                mealMap.put("isOpen", ck.getIsOpen());
                meals.add(mealMap);
            }
        }

        map.put("meals", meals);
        return map;
    }
}
