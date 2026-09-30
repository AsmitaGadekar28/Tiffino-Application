package com.tiffino.tiffino.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class EmailValidationService {

    private static final String API_KEY = "982ffd8d144d44b0c87784577dfc678c"; //  apni key daalo
    private static final String API_URL = "https://apilayer.net/api/check";

    public boolean isEmailDeliverable(String email) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("access_key", API_KEY)
                    .queryParam("email", email)
                    .queryParam("smtp", 1)
                    .queryParam("format", 1)
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            Boolean formatValid = (Boolean) response.get("format_valid");
            Boolean smtpCheck = (Boolean) response.get("smtp_check");

            return Boolean.TRUE.equals(formatValid) && Boolean.TRUE.equals(smtpCheck);
        } catch (Exception e) {
            System.out.println("❌ Email validation failed: " + e.getMessage());
            return false;
        }
    }
}
