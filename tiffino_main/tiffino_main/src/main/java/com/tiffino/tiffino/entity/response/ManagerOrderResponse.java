package com.tiffino.tiffino.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerOrderResponse {
    private Long orderId;
    private String userName;
    private double totalAmount;
    private String status;
    private LocalDateTime orderTime;
    private String orderDate;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private List<OrderItemResponse> items;
    private List<String> allergies;
}
