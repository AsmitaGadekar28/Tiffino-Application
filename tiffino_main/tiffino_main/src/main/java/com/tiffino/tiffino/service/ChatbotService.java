package com.tiffino.tiffino.service;
/**
 import com.tiffino.tiffino.config.JwtUtil;
 import com.tiffino.tiffino.dto.BotResponseDto;
 import com.tiffino.tiffino.entity.*;
 import com.tiffino.tiffino.repository.*;
 import lombok.RequiredArgsConstructor;
 import org.springframework.messaging.simp.SimpMessagingTemplate;
 import org.springframework.stereotype.Service;
 import org.springframework.web.multipart.MultipartFile;

 import java.io.IOException;
 import java.util.List;

 @Service
 @RequiredArgsConstructor
 public class ChatbotService {

 private final IssueReportRepository issueRepo;
 private final CloudinaryService cloudinaryService;
 private final JwtUtil jwtUtil;
 private final SimpMessagingTemplate messagingTemplate;

 private final UserRepo userRepo;
 private final OrderRepo orderRepository;

 //START CHAT WITH USER + LAST ORDER
 public BotResponseDto startChat(String authHeader) {
 String email = extractEmail(authHeader);
 User user = userRepo.findByEmail(email).orElse(null);
 Order lastOrder = getLastDeliveredOrder(email);

 if (lastOrder == null) {
 return new BotResponseDto(
 "Hi " + (user != null ? user.getUserName() : "there") +
 ", I couldn’t find any delivered order linked to your account.",
 List.of()
 );
 }

 return new BotResponseDto(
 "Hi " + user.getUserName() +
 "! Your last delivered order (ID: " + lastOrder.getOrderId() + ") is detected. What issue did you face?",
 List.of("wrong item delivered", "food spoiled", "found insects", "bad taste")
 );
 }

 // CHAT FLOW OPTIONS
 public BotResponseDto handleUserSelection(String rawInput, String authHeader) {

 String intent = normalizeToIntent(rawInput);

 switch (intent) {

 case "wrong item delivered":
 return new BotResponseDto(
 "I understand the wrong item arrived. Can you upload a photo or describe the issue?",
 List.of()
 );

 case "food spoiled":
 return new BotResponseDto(
 "Sorry about that. Was the food a) visibly spoiled/rotten, b) had bad smell, c) arrived cold?",
 List.of("a) visibly spoiled/rotten", "b) had bad smell", "c) arrived cold")
 );

 case "spoiled_visibly":
 case "spoiled_smell":
 saveIssueWithoutPhoto(IssueType.FOOD_SPOILED, authHeader);
 return new BotResponseDto(
 "Sorry for that, you will get a response from our support team shortly.",
 List.of()
 );

 case "arrived_cold":
 saveIssueWithoutPhoto(IssueType.ARRIVED_COLD, authHeader);
 return new BotResponseDto(
 "Extremely sorry for that. We'll ensure your next order is fresh & warm!",
 List.of()
 );

 case "found insects":
 return new BotResponseDto(
 "I'm really sorry. Please send a clear photo & confirm whether the food was consumed.",
 List.of()
 );

 case "bad taste":
 return new BotResponseDto(
 "Sorry to hear that. Was it flavour or texture?",
 List.of("flavor", "texture")
 );

 case "bad_taste_flavor":
 case "bad_taste_texture":
 saveIssueWithoutPhoto(IssueType.BAD_TASTE, authHeader);
 return new BotResponseDto(
 "Sorry for that, our support team will reach out shortly.",
 List.of()
 );

 default:
 return new BotResponseDto(
 "Sorry, I didn't understand. Please choose again.",
 List.of("wrong item delivered", "food spoiled", "found insects", "bad taste")
 );
 }
 }

 //ISSUE WITH PHOTO
 public BotResponseDto reportIssue(String authHeader,
 String issueType,
 String message,
 String extraChoice,
 MultipartFile photoFile) throws IOException {

 User user = getUserFromToken(authHeader);
 Order lastOrder = getLastDeliveredOrder(user.getEmail());

 String photoUrl = null;

 if (photoFile != null && !photoFile.isEmpty()) {
 photoUrl = cloudinaryService.uploadFile(photoFile);
 }

 IssueType typeEnum = mapToIssueType(issueType);

 IssueReport saved = issueRepo.save(
 IssueReport.builder()
 .userEmail(user.getEmail())
 .userName(user.getUserName())
 .phoneNumber(user.getPhoneNo())
 .address(user.getAddress())
 .orderId(lastOrder != null ? lastOrder.getOrderId() : null)
 .issueType(typeEnum)
 .extraChoice(extraChoice)
 .message(message)
 .photoUrl(photoUrl)
 .status("PENDING")
 .managerNotified(true)
 .build()
 );

 messagingTemplate.convertAndSend("/topic/manager/issues", saved);

 return new BotResponseDto(
 "Thanks " + user.getUserName() + ", our team will reach out shortly.",
 List.of()
 );
 }

 // ISSUE WITHOUT PHOTO
 private void saveIssueWithoutPhoto(IssueType typeEnum, String authHeader) {

 User user = getUserFromToken(authHeader);
 Order lastOrder = getLastDeliveredOrder(user.getEmail());

 IssueReport saved = issueRepo.save(
 IssueReport.builder()
 .userEmail(user.getEmail())
 .userName(user.getUserName())
 .phoneNumber(user.getPhoneNo())
 .address(user.getAddress())
 .orderId(lastOrder != null ? lastOrder.getOrderId() : null)
 .issueType(typeEnum)
 .status("PENDING")
 .managerNotified(true)
 .build()
 );

 messagingTemplate.convertAndSend("/topic/manager/issues", saved);
 }

 // TOKEN → EMAIL
 private String extractEmail(String authHeader) {
 try {
 if (authHeader != null && authHeader.startsWith("Bearer ")) {
 return jwtUtil.extractUsername(authHeader.substring(7));
 }
 } catch (Exception ignored) {}
 return "GUEST";
 }

 // TOKEN → USER OBJECT
 private User getUserFromToken(String authHeader) {
 String email = extractEmail(authHeader);
 return userRepo.findByEmail(email)
 .orElseThrow(() -> new RuntimeException("User not found"));
 }

 // LAST DELIVERED ORDER
 private Order getLastDeliveredOrder(String email) {
 return orderRepository.findTopByUser_EmailAndStatusOrderByDeliveredAtDesc(email, "DELIVERED");
 }

 //INTENT NORMALIZER
 private String normalizeToIntent(String s) {
 if (s == null) return "";
 s = s.trim().toLowerCase();

 if (s.contains("wrong")) return "wrong item delivered";
 if (s.contains("spoil")) return "food spoiled";
 if (s.contains("insect") || s.contains("bug")) return "found insects";
 if (s.contains("taste")) return "bad taste";
 if (s.contains("rotten")) return "spoiled_visibly";
 if (s.contains("smell")) return "spoiled_smell";
 if (s.contains("cold")) return "arrived_cold";
 if (s.contains("flavor") || s.contains("flavour")) return "bad_taste_flavor";
 if (s.contains("texture")) return "bad_taste_texture";

 return s;
 }

 // MAP STRING TO ENUM
 private IssueType mapToIssueType(String s) {
 s = s.toLowerCase();

 if (s.contains("wrong")) return IssueType.WRONG_ITEM_DELIVERED;
 if (s.contains("spoil") || s.contains("rotten") || s.contains("smell")) return IssueType.FOOD_SPOILED;
 if (s.contains("insect") || s.contains("bug")) return IssueType.FOUND_INSECTS;
 if (s.contains("taste") || s.contains("flavor")) return IssueType.BAD_TASTE;
 if (s.contains("cold")) return IssueType.ARRIVED_COLD;

 throw new IllegalArgumentException("Unknown issue type: " + s);
 }
 }
 **/
/**

 import com.tiffino.tiffino.config.JwtUtil;
 import com.tiffino.tiffino.dto.BotResponseDto;
 import com.tiffino.tiffino.entity.*;
 import com.tiffino.tiffino.repository.*;
 import lombok.RequiredArgsConstructor;
 import org.springframework.messaging.simp.SimpMessagingTemplate;
 import org.springframework.stereotype.Service;
 import org.springframework.web.multipart.MultipartFile;

 import java.io.IOException;
 import java.util.List;

 @Service
 @RequiredArgsConstructor
 public class ChatbotService {

 private final IssueReportRepository issueRepo;
 private final CloudinaryService cloudinaryService;
 private final JwtUtil jwtUtil;
 private final SimpMessagingTemplate messagingTemplate;

 private final UserRepo userRepo;
 private final OrderRepo orderRepository;


 // ✅ START CHAT WITH USER + LAST ORDER
 public BotResponseDto startChat(String authHeader) {
 String email = extractEmail(authHeader);
 User user = userRepo.findByEmail(email).orElse(null);
 Order lastOrder = getLastDeliveredOrder(email);

 if (lastOrder == null) {
 return new BotResponseDto(
 "Hi " + (user != null ? user.getUserName() : "there")
 + ", I couldn’t find any delivered order linked to your account.",
 List.of()
 );
 }

 return new BotResponseDto(
 "Hi " + user.getUserName()
 + "! Your last delivered order (ID: " + lastOrder.getOrderId()
 + ") is detected. What issue did you face?",
 List.of("wrong item delivered", "food spoiled", "found insects", "bad taste")
 );
 }


 //** ✅ MAIN CHAT FLOW — handle user text/options
 public BotResponseDto handleUserSelection(String rawInput, String authHeader) {

 String intent = normalizeToIntent(rawInput);

 switch (intent) {

 case "wrong item delivered":
 return new BotResponseDto(
 "I understand the wrong item arrived. Can you upload a photo or describe the issue?",
 List.of()
 );

 case "food spoiled":
 return new BotResponseDto(
 "Sorry about that. Was the food:\n a) visibly spoiled/rotten\n b) had bad smell\n c) arrived cold?",
 List.of("a) visibly spoiled/rotten", "b) had bad smell", "c) arrived cold")
 );

 case "spoiled_visibly":
 case "spoiled_smell":
 saveIssueWithoutPhoto(IssueType.FOOD_SPOILED, authHeader);
 return new BotResponseDto(
 "Sorry for that, you will get a response from our team shortly.",
 List.of()
 );

 case "arrived_cold":
 saveIssueWithoutPhoto(IssueType.ARRIVED_COLD, authHeader);
 return new BotResponseDto(
 "Extremely sorry for that. We'll ensure your next order is fresh & warm!",
 List.of()
 );

 case "found insects":
 return new BotResponseDto(
 "I’m really sorry. Please upload a clear photo & tell us if the food was consumed.",
 List.of()
 );

 case "bad taste":
 return new BotResponseDto(
 "Sorry to hear that. Was the problem with flavour or texture?",
 List.of("flavor", "texture")
 );

 case "bad_taste_flavor":
 case "bad_taste_texture":
 saveIssueWithoutPhoto(IssueType.BAD_TASTE, authHeader);
 return new BotResponseDto(
 "Sorry for that, our support team will reach out shortly.",
 List.of()
 );

 default:
 return new BotResponseDto(
 "Sorry, I didn’t understand. Please choose again.",
 List.of("wrong item delivered", "food spoiled", "found insects", "bad taste")
 );
 }
 }


 //** ✅ ISSUE WITH PHOTO
 public BotResponseDto reportIssue(String authHeader,
 String issueType,
 String message,
 String extraChoice,
 MultipartFile photoFile) throws IOException {

 User user = getUserFromToken(authHeader);
 Order lastOrder = getLastDeliveredOrder(user.getEmail());

 String cloudKitchenId = (lastOrder != null && lastOrder.getCloudKitchen() != null)
 ? lastOrder.getCloudKitchen().getCloudKitchenId()
 : null;

 String photoUrl = null;

 if (photoFile != null && !photoFile.isEmpty()) {
 photoUrl = cloudinaryService.uploadFile(photoFile);
 }

 IssueType typeEnum = mapToIssueType(issueType);

 IssueReport saved = issueRepo.save(
 IssueReport.builder()
 .userEmail(user.getEmail())
 .userName(user.getUserName())
 .phoneNumber(user.getPhoneNo())
 .address(user.getAddress())
 .orderId(lastOrder != null ? lastOrder.getOrderId() : null)

 .cloudKitchenId(cloudKitchenId)

 .issueType(typeEnum)
 .extraChoice(extraChoice)
 .message(message)
 .photoUrl(photoUrl)
 .status("PENDING")
 .managerNotified(true)
 .build()
 );

 messagingTemplate.convertAndSend("/topic/manager/issues", saved);

 return new BotResponseDto(
 "Thanks " + user.getUserName() + ", our team will reach out shortly.",
 List.of()
 );
 }


 //** ✅ ISSUE WITHOUT PHOTO
 private void saveIssueWithoutPhoto(IssueType typeEnum, String authHeader) {

 User user = getUserFromToken(authHeader);
 Order lastOrder = getLastDeliveredOrder(user.getEmail());

 String cloudKitchenId = (lastOrder != null && lastOrder.getCloudKitchen() != null)
 ? lastOrder.getCloudKitchen().getCloudKitchenId()
 : null;

 IssueReport saved = issueRepo.save(
 IssueReport.builder()
 .userEmail(user.getEmail())
 .userName(user.getUserName())
 .phoneNumber(user.getPhoneNo())
 .address(user.getAddress())
 .orderId(lastOrder != null ? lastOrder.getOrderId() : null)

 .cloudKitchenId(cloudKitchenId)

 .issueType(typeEnum)
 .status("PENDING")
 .managerNotified(true)
 .build()
 );

 messagingTemplate.convertAndSend("/topic/manager/issues", saved);
 }


 //** ✅ TOKEN → EMAIL
 private String extractEmail(String authHeader) {
 try {
 if (authHeader != null && authHeader.startsWith("Bearer ")) {
 return jwtUtil.extractUsername(authHeader.substring(7));
 }
 } catch (Exception ignored) {}
 return "GUEST";
 }

 private User getUserFromToken(String authHeader) {
 String email = extractEmail(authHeader);
 return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
 }

 private Order getLastDeliveredOrder(String email) {
 return orderRepository.findTopByUser_EmailAndStatusOrderByDeliveredAtDesc(email, "DELIVERED");
 }



 private String normalizeToIntent(String s) {
 if (s == null) return "";
 s = s.trim().toLowerCase();

 // MAIN ISSUE MATCH
 if (s.contains("wrong")) return "wrong item delivered";
 if (s.contains("spoil") && !s.startsWith("a)")) return "food spoiled";
 if (s.contains("insect") || s.contains("bug")) return "found insects";
 if (s.contains("bad taste") || s.equals("taste")) return "bad taste";

 // SUB ISSUE MATCH  ✅ FIX
 if (s.startsWith("a)")) return "spoiled_visibly";
 if (s.startsWith("b)")) return "spoiled_smell";
 if (s.startsWith("c)")) return "arrived_cold";

 // Keyword fallback
 if (s.contains("rotten") || s.contains("visibly")) return "spoiled_visibly";
 if (s.contains("smell")) return "spoiled_smell";
 if (s.contains("cold")) return "arrived_cold";

 // Bad taste sub-options
 if (s.contains("flavor") || s.contains("flavour")) return "bad_taste_flavor";
 if (s.contains("texture")) return "bad_taste_texture";

 return s;
 }



 //** ✅ MAP STRING → ENUM
 private IssueType mapToIssueType(String s) {
 s = s.toLowerCase();

 if (s.contains("wrong")) return IssueType.WRONG_ITEM_DELIVERED;
 if (s.contains("spoil") || s.contains("rotten") || s.contains("smell")) return IssueType.FOOD_SPOILED;
 if (s.contains("insect") || s.contains("bug")) return IssueType.FOUND_INSECTS;
 if (s.contains("taste") || s.contains("flavor")) return IssueType.BAD_TASTE;
 if (s.contains("cold")) return IssueType.ARRIVED_COLD;

 throw new IllegalArgumentException("Unknown issue type: " + s);
 }
 }
 **/

import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.dto.BotResponseDto;
import com.tiffino.tiffino.entity.*;
import com.tiffino.tiffino.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final IssueReportRepository issueRepo;
    private final CloudinaryService cloudinaryService;
    private final JwtUtil jwtUtil;
    private final SimpMessagingTemplate messagingTemplate;

    private final UserRepo userRepo;
    private final OrderRepo orderRepository;

    // ✅ Store selected order from frontend
    private final Map<String, Order> manualOrderMap = new HashMap<>();


    /** ✅ SET ORDER FROM UI (URL orderId) */
    public boolean setSelectedOrder(String authHeader, Long orderId) {
        String email = extractEmail(authHeader);

        Order order = orderRepository.findByOrderIdAndUser_Email(orderId, email).orElse(null);

        if (order == null) return false;
        if (!"DELIVERED".equalsIgnoreCase(order.getStatus())) return false;

        // ✅ Save order manually for this chat session
        manualOrderMap.put(email, order);
        return true;
    }


    /** ✅ START CHAT WITH USER + SELECTED ORDER */
    public BotResponseDto startChat(String authHeader) {
        String email = extractEmail(authHeader);
        User user = userRepo.findByEmail(email).orElse(null);
        Order selected = getLastDeliveredOrder(email);

        if (selected == null) {
            /**  return new BotResponseDto(
             "Hi " + (user != null ? user.getUserName() : "there")
             + ", I couldn’t find any delivered order linked to your account.",
             List.of()
             );**/
            return new BotResponseDto(
                    "Hi, I'm FoodCare 😊\n"
                            + "I couldn't find any delivered order linked to your account.",
                    List.of()
            );
        }

        /** return new BotResponseDto(
         "Hi " + user.getUserName()
         + "! Your order (ID: " + selected.getOrderId()
         + ") is selected. What issue did you face?",
         List.of("wrong item delivered", "food spoiled", "found insects", "bad taste")
         );**/
        return new BotResponseDto(
                "Hi, I'm FoodCare 😊\n"
                        + "I can help with order mismatches, spoiled food, insects, and more.\n"
                        + "What happened with your order?",
                List.of("wrong item delivered", "food spoiled", "found insects", "bad taste")
        );
    }


    /** ✅ MAIN CHAT FLOW */
    public BotResponseDto handleUserSelection(String rawInput, String authHeader) {

        // ✅ OKAY → simple reply
        if (rawInput.equalsIgnoreCase("okay")) {
            return new BotResponseDto(
                    "Alright! Let me know whenever you need help.",
                    List.of()
            );
        }

        // ✅ TALK TO SUPPORT → simple reply
        if (rawInput.toLowerCase().contains("support")) {
            return new BotResponseDto(
                    "Connecting you to support… (feature coming soon)",
                    List.of()
            );
        }

        String intent = normalizeToIntent(rawInput);

        switch (intent) {

            case "wrong item delivered":
                return new BotResponseDto(
                        "I understand the wrong item arrived. Can you upload a photo or describe the issue?",
                        List.of()
                );

            case "food spoiled":
                return new BotResponseDto(
                        "Sorry about that. Was the food:\n a) visibly spoiled/rotten\n b) had bad smell\n c) arrived cold?",
                        List.of("a) visibly spoiled/rotten", "b) had bad smell", "c) arrived cold")
                );

            case "spoiled_visibly":
            case "spoiled_smell":
                saveIssueWithoutPhoto(IssueType.FOOD_SPOILED, authHeader);
                return new BotResponseDto(
                        "Sorry for that, you will get a response from our team shortly.",
                        List.of()
                );

            case "arrived_cold":
                saveIssueWithoutPhoto(IssueType.ARRIVED_COLD, authHeader);
                return new BotResponseDto(
                        "Extremely sorry for that. We'll ensure your next order is fresh & warm!",
                        List.of()
                );

            case "found insects":
                return new BotResponseDto(
                        "I’m really sorry. Please upload a clear photo & tell us if the food was consumed.",
                        List.of()
                );

            case "bad taste":
                return new BotResponseDto(
                        "Sorry to hear that. Was the problem with flavour or texture?",
                        List.of("flavor", "texture")
                );

            case "bad_taste_flavor":
            case "bad_taste_texture":
                saveIssueWithoutPhoto(IssueType.BAD_TASTE, authHeader);
                return new BotResponseDto(
                        "Sorry for that, our support team will reach out shortly.",
                        List.of()
                );

            default:
                return new BotResponseDto(
                        "Sorry, I didn’t understand. Please choose again.",
                        List.of("wrong item delivered", "food spoiled", "found insects", "bad taste")
                );
        }
    }



    /** ✅ REPORT ISSUE WITH PHOTO */
    public BotResponseDto reportIssue(String authHeader,
                                      String issueType,
                                      String message,
                                      String extraChoice,
                                      MultipartFile photoFile) throws IOException {

        User user = getUserFromToken(authHeader);
        Order order = getLastDeliveredOrder(user.getEmail());

        String cloudKitchenId =
                (order != null && order.getCloudKitchen() != null)
                        ? order.getCloudKitchen().getCloudKitchenId() : null;

        String photoUrl = (photoFile != null && !photoFile.isEmpty())
                ? cloudinaryService.uploadFile(photoFile)
                : null;

        IssueType typeEnum = mapToIssueType(issueType);

        IssueReport saved = issueRepo.save(
                IssueReport.builder()
                        .userEmail(user.getEmail())
                        .userName(user.getUserName())
                        .phoneNumber(user.getPhoneNo())
                        .address(user.getAddress())
                        .orderId(order != null ? order.getOrderId() : null)
                        .cloudKitchenId(cloudKitchenId)
                        .issueType(typeEnum)
                        .extraChoice(extraChoice)
                        .message(message)
                        .photoUrl(photoUrl)
                        .status("PENDING")
                        .managerNotified(true)
                        .build()
        );

        messagingTemplate.convertAndSend("/topic/manager/issues", saved);

        return new BotResponseDto(
                "Thanks " + user.getUserName() + ", our team will reach out shortly.",
                List.of()
        );
    }


    /** ✅ SAVE ISSUE WITHOUT PHOTO */
    private void saveIssueWithoutPhoto(IssueType typeEnum, String authHeader) {

        User user = getUserFromToken(authHeader);
        Order order = getLastDeliveredOrder(user.getEmail());

        String cloudKitchenId =
                (order != null && order.getCloudKitchen() != null)
                        ? order.getCloudKitchen().getCloudKitchenId() : null;

        IssueReport saved = issueRepo.save(
                IssueReport.builder()
                        .userEmail(user.getEmail())
                        .userName(user.getUserName())
                        .phoneNumber(user.getPhoneNo())
                        .address(user.getAddress())
                        .orderId(order != null ? order.getOrderId() : null)
                        .cloudKitchenId(cloudKitchenId)
                        .issueType(typeEnum)
                        .status("PENDING")
                        .managerNotified(true)
                        .build()
        );

        messagingTemplate.convertAndSend("/topic/manager/issues", saved);
    }


    /** ✅ TOKEN → EMAIL */
    private String extractEmail(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtUtil.extractUsername(authHeader.substring(7));
        }
        return "GUEST";
    }

    private User getUserFromToken(String authHeader) {
        String email = extractEmail(authHeader);
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    /** ✅ GET SELECTED ORDER FROM MAP OR LAST DELIVERED */
    private Order getLastDeliveredOrder(String email) {

        if (manualOrderMap.containsKey(email)) {
            return manualOrderMap.get(email);
        }

        return orderRepository
                .findTopByUser_EmailAndStatusOrderByDeliveredAtDesc(email, "DELIVERED");
    }


    /** ✅ RAW TEXT → INTENT */
    private String normalizeToIntent(String s) {
        if (s == null) return "";
        s = s.trim().toLowerCase();

        if (s.contains("wrong")) return "wrong item delivered";
        if (s.contains("spoil") && !s.startsWith("a)")) return "food spoiled";
        if (s.contains("insect") || s.contains("bug")) return "found insects";
        if (s.contains("bad taste") || s.equals("taste")) return "bad taste";

        if (s.startsWith("a)")) return "spoiled_visibly";
        if (s.startsWith("b)")) return "spoiled_smell";
        if (s.startsWith("c)")) return "arrived_cold";

        if (s.contains("rotten") || s.contains("visibly")) return "spoiled_visibly";
        if (s.contains("smell")) return "spoiled_smell";
        if (s.contains("cold")) return "arrived_cold";

        if (s.contains("flavor") || s.contains("flavour")) return "bad_taste_flavor";
        if (s.contains("texture")) return "bad_taste_texture";

        return s;
    }


    /** ✅ STRING → ENUM */
    private IssueType mapToIssueType(String s) {
        s = s.toLowerCase();

        if (s.contains("wrong")) return IssueType.WRONG_ITEM_DELIVERED;
        if (s.contains("spoil") || s.contains("rotten") || s.contains("smell"))
            return IssueType.FOOD_SPOILED;
        if (s.contains("insect") || s.contains("bug"))
            return IssueType.FOUND_INSECTS;
        if (s.contains("taste") || s.contains("flavor"))
            return IssueType.BAD_TASTE;
        if (s.contains("cold"))
            return IssueType.ARRIVED_COLD;

        throw new IllegalArgumentException("Unknown issue type: " + s);
    }
}
