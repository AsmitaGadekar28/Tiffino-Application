package com.tiffino.tiffino.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder

@NoArgsConstructor
@AllArgsConstructor
public class CuisineDTO {
    private Long cuisineId;
    private String name;
    private String description;
    private String photo;
    private String stateName;

    @JsonIgnore
    private MultipartFile file;
}
