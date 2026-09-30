package com.tiffino.tiffino.service;

import com.tiffino.tiffino.dto.CuisineDTO;

import com.tiffino.tiffino.entity.Cuisine;
import com.tiffino.tiffino.repository.CuisineRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
/**
@Service
@RequiredArgsConstructor
public class CuisineService {


    private final CuisineRepo cuisineRepository;

    public CuisineDTO createCuisine(CuisineDTO dto) {
        Cuisine cuisine = new Cuisine();
        cuisine.setName(dto.getName());
        cuisine.setDescription(dto.getDescription());

        Cuisine saved = cuisineRepository.save(cuisine);
        dto.setCuisineId(saved.getCuisineId());
        return dto;
    }

    public List<CuisineDTO> getAllCuisines() {
        return cuisineRepository.findAll().stream().map(c -> {
            CuisineDTO dto = new CuisineDTO();
            dto.setCuisineId(c.getCuisineId());
            dto.setName(c.getName());
            dto.setDescription(c.getDescription());
            return dto;
        }).collect(Collectors.toList());
    }

}
**/

@Service
@RequiredArgsConstructor
public class CuisineService {

    private final CuisineRepo cuisineRepository;
    private final CloudinaryService cloudinaryService; // Cloudinary injection

    // Save cuisine with photo
    public CuisineDTO saveCuisine(CuisineDTO dto) throws IOException {
        Cuisine cuisine = new Cuisine();
        cuisine.setName(dto.getName());
        cuisine.setDescription(dto.getDescription());
        cuisine.setStateName(dto.getStateName());

        // Upload photo if file is present
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            String photoUrl = cloudinaryService.uploadFile(dto.getFile());
            cuisine.setPhoto(photoUrl);
        }

        Cuisine saved = cuisineRepository.save(cuisine);

        dto.setCuisineId(saved.getCuisineId());
        dto.setPhoto(saved.getPhoto());
        return dto;
    }

    // Get all cuisines
    public List<CuisineDTO> getAllCuisines() {
        return cuisineRepository.findAll().stream().map(c -> {
            return CuisineDTO.builder()
                    .cuisineId(c.getCuisineId())
                    .name(c.getName())
                    .description(c.getDescription())
                    .photo(c.getPhoto())
                    .stateName(c.getStateName())
                    .build();
        }).collect(Collectors.toList());
    }
}
