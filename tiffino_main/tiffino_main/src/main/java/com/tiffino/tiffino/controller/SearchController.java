package com.tiffino.tiffino.controller;
/**
import com.tiffino.tiffino.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/searchFilterForUser")
    public List<Map<String, Object>> searchFilter(@RequestBody Map<String, List<String>> request) {
        List<String> cuisineNames = request.get("cuisineNames");
        List<String> cloudKitchenNames = request.get("cloudKitchenNames");

        return searchService.searchFilterForUser(cuisineNames, cloudKitchenNames);
    }
}
**/

import com.tiffino.tiffino.service.SearchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/searchFilterForUser")
    public List<Map<String, Object>> searchFilterByState(@RequestBody Map<String, List<String>> request) {
        List<String> stateNames = request.get("stateNames");
        List<String> cloudKitchenNames = request.get("cloudKitchenNames");

        return searchService.searchFilterForUserByState(stateNames, cloudKitchenNames);
    }
}
