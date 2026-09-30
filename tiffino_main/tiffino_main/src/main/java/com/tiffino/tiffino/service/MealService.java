package com.tiffino.tiffino.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tiffino.tiffino.dto.MealDTO;
import com.tiffino.tiffino.entity.CloudKitchenMeal;
import com.tiffino.tiffino.entity.Cuisine;
import com.tiffino.tiffino.entity.Meal;
import com.tiffino.tiffino.entity.response.MealWithCuisineResponse;
import com.tiffino.tiffino.repository.CloudKitchenMealRepo;
import com.tiffino.tiffino.repository.CuisineRepo;

import com.tiffino.tiffino.repository.MealRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepo mealRepository;
    private final CuisineRepo cuisineRepository;
    private final Cloudinary cloudinary;


    private String uploadFile(MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                var result = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                return result.get("secure_url").toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
        return null;
    }

    public MealDTO createMeal(MealDTO dto) {
        Cuisine cuisine = cuisineRepository.findById(dto.getCuisineId())
                .orElseThrow(() -> new RuntimeException("Cuisine not found"));

        Meal meal = new Meal();
        meal.setName(dto.getName());
        meal.setDescription(dto.getDescription());
        meal.setNutritionalInformation(dto.getNutritionalInformation());
        meal.setPrice(dto.getPrice());
        meal.setCuisine(cuisine);

        // Photo upload
        String photoUrl = uploadFile(dto.getPhoto());
        meal.setPhotos(photoUrl);

        Meal saved = mealRepository.save(meal);

        MealDTO response = new MealDTO();
        response.setMealId(saved.getMealId());
        response.setName(saved.getName());
        response.setDescription(saved.getDescription());
        response.setNutritionalInformation(saved.getNutritionalInformation());
        response.setPrice(saved.getPrice());
        response.setCuisineId(saved.getCuisine().getCuisineId());

        return response;
    }
  /**
    //getAllAvailableMealsWithCuisines
    public List<MealWithCuisineResponse> getAllAvailableMealsWithCuisine() {

        List<CloudKitchenMeal> meals = cloudKitchenMealRepo.findAllAvailable();

        return meals.stream().map(meal -> {
            String cloudKitchenName = meal.getCloudKitchen().getCity() + "_" + meal.getCloudKitchen().getDivision();

            return new MealWithCuisineResponse(
                    meal.getMeal().getMealId(),
                    meal.getMeal().getName(),
                    meal.getMeal().getPrice(),
                    meal.getMeal().getPhotos(),
                    meal.getMeal().getNutritionalInformation(),
                    meal.getMeal().getDescription(),
                    meal.getMeal().getCuisine().getCuisineId(),
                    meal.getMeal().getCuisine().getName(),
                    meal.getCloudKitchen().getCloudKitchenId(),
                    cloudKitchenName
            );
        }).collect(Collectors.toList());
    }**/
}
