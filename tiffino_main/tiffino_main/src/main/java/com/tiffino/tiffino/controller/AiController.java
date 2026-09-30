package com.tiffino.tiffino.controller;

import com.tiffino.tiffino.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // Accepts JSON: { "prompt": "How do I register?" }
    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");
        String resp = aiService.getAiResponse(prompt);
        return ResponseEntity.ok(resp);
    }
}

 
