package com.tiffino.tiffino.controller;
/**
 import com.tiffino.tiffino.dto.BotResponseDto;
 import com.tiffino.tiffino.service.ChatbotService;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.*;
 import org.springframework.web.multipart.MultipartFile;

 import java.io.IOException;
 import java.util.Map;

 @RestController
 @RequestMapping("/api/chatbot")
 @RequiredArgsConstructor
 public class ChatbotController {

 private final ChatbotService chatbotService;

 //START CHAT (now uses user token)
 @GetMapping("/start")
 public ResponseEntity<BotResponseDto> startChat(
 @RequestHeader("Authorization") String authHeader) {
 return ResponseEntity.ok(chatbotService.startChat(authHeader));
 }

 //✅ USER SELECTS OPTION
 @PostMapping("/reply")
 public ResponseEntity<BotResponseDto> reply(
 @RequestHeader("Authorization") String authHeader,
 @RequestBody Map<String, String> body) {

 String selectedOption = body.get("selectedOption");
 return ResponseEntity.ok(chatbotService.handleUserSelection(selectedOption, authHeader));
 }

 // USER UPLOADS PHOTO
 @PostMapping("/report-issue")
 public ResponseEntity<BotResponseDto> reportIssue(
 @RequestHeader("Authorization") String authHeader,
 @RequestParam("issueType") String issueType,
 @RequestParam(value = "message", required = false) String message,
 @RequestParam(value = "extraChoice", required = false) String extraChoice,
 @RequestParam(value = "photoFile", required = false) MultipartFile photoFile)
 throws IOException {

 return ResponseEntity.ok(
 chatbotService.reportIssue(authHeader, issueType, message, extraChoice, photoFile)
 );
 }
 }
 **/
/**
 @RestController
 @RequestMapping("/api/chatbot")
 @RequiredArgsConstructor
 public class ChatbotController {

 private final ChatbotService chatbotService;

 @GetMapping("/start")
 public BotResponseDto startChat(@RequestHeader("Authorization") String authHeader) {
 return chatbotService.startChat(authHeader);
 }

 @PostMapping("/reply")
 public BotResponseDto handleUserReply(
 @RequestHeader("Authorization") String authHeader,
 @RequestBody Map<String, String> body) {

 String selectedOption = body.get("selectedOption");
 return chatbotService.handleUserSelection(selectedOption, authHeader);
 }

 @PostMapping("/report-issue")
 public BotResponseDto reportIssue(
 @RequestHeader("Authorization") String authHeader,
 @RequestParam String issueType,
 @RequestParam(required = false) String message,
 @RequestParam(required = false) String extraChoice,
 @RequestParam(required = false) MultipartFile photoFile) throws IOException {

 return chatbotService.reportIssue(authHeader, issueType, message, extraChoice, photoFile);
 }
 }
 **/


import com.tiffino.tiffino.dto.BotResponseDto;
import com.tiffino.tiffino.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;


    /** ✅ 1. Start chat (auto detects delivered order OR selected order) */
    @GetMapping("/start")
    public ResponseEntity<BotResponseDto> startChat(
            @RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(chatbotService.startChat(authHeader));
    }


    /** ✅ 2. Handle user selection / typed message */
    @PostMapping("/reply")
    public ResponseEntity<BotResponseDto> reply(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {

        String selectedOption = body.get("selectedOption");
        return ResponseEntity.ok(chatbotService.handleUserSelection(selectedOption, authHeader));
    }


    /** ✅ 3. Report Issue with Photo */
    @PostMapping(value = "/report-issue", consumes = "multipart/form-data")
    public ResponseEntity<BotResponseDto> reportIssue(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String issueType,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String extraChoice,
            @RequestParam(required = false) MultipartFile photoFile
    ) throws IOException {

        return ResponseEntity.ok(
                chatbotService.reportIssue(authHeader, issueType, message, extraChoice, photoFile)
        );
    }


    /** ✅ 4. Set order manually from frontend (URL orderId) */
    @PostMapping("/set-order")
    public ResponseEntity<?> setOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {

        Long orderId = Long.valueOf(body.get("orderId").toString());

        boolean ok = chatbotService.setSelectedOrder(authHeader, orderId);

        if (!ok)
            return ResponseEntity.badRequest().body("Invalid or undelivered order");

        return ResponseEntity.ok("Order selected");
    }
}
