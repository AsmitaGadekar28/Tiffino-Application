package com.tiffino.tiffino.controller;

import com.tiffino.tiffino.entity.request.OrderRequest;
import com.tiffino.tiffino.entity.response.CreateOrderResponse;
import com.tiffino.tiffino.entity.response.ManagerOrderResponse;
import com.tiffino.tiffino.entity.response.UserOrderResponse;
import com.tiffino.tiffino.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ================= CREATE ORDER (USER) =================
    @PostMapping("/create")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody OrderRequest orderRequest
    ) {
        CreateOrderResponse response = orderService.createOrder(token, orderRequest);
        return ResponseEntity.ok(response);
    }
 /**  @PostMapping("/create")
   public ResponseEntity<List<CreateOrderResponse>> createOrder(
           @RequestHeader("Authorization") String token,
           @RequestBody OrderRequest request
   ) {
       List<CreateOrderResponse> responses = orderService.createOrder(token, request);
       return ResponseEntity.ok(responses);
   }**/

 // ================= GET ORDERS FOR LOGGED-IN USER =================
 @GetMapping("/my-orders")
 public ResponseEntity<List<UserOrderResponse>> getUserOrders(
         @RequestHeader("Authorization") String token
 ) {
     List<UserOrderResponse> orders = orderService.getAllOrdersByUser(token);
     return ResponseEntity.ok(orders);
 }

    // ================= GET ALL ORDERS FOR MANAGER =================
    @GetMapping("/all")
    public ResponseEntity<List<ManagerOrderResponse>> getAllOrdersForManager(
            @RequestHeader("Authorization") String token
    ) {
        List<ManagerOrderResponse> orders = orderService.getAllOrdersForManager(token);
        return ResponseEntity.ok(orders);
    }

    // ================= ACCEPT ORDER =================
    @PutMapping("/acceptOrder/{orderId}")
    public ResponseEntity<String> acceptOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId
    ) {
        String response = orderService.acceptOrder(token, orderId);
        return ResponseEntity.ok(response);
    }

    // ================= PREPARING ORDER =================
    @PutMapping("/preparingOrder/{orderId}")
    public ResponseEntity<String> preparingOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId
    ) {
        String response = orderService.preparingOrder(token, orderId);
        return ResponseEntity.ok(response);
    }

    // ================= GET ORDER SUMMARY =================
    @GetMapping("/manager/getOrderSummary")
    public ResponseEntity<Map<String, Object>> getOrderSummary(
            @RequestHeader("Authorization") String token
    ) {
        Map<String, Object> summary = orderService.getOrderSummary(token);
        return ResponseEntity.ok(summary);
    }


}
