package com.tiffino.tiffino.dto;

import lombok.Data;
import java.util.List;

@Data
public class SearchFilterRequestDTO {
    private List<String> stateNames;
    private List<String> cities;
    private List<String> divisions;
}
