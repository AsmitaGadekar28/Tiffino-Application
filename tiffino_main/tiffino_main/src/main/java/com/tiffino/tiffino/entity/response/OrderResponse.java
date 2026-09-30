package com.tiffino.tiffino.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String userName; // manager only
    private double totalAmount;
    private String status;
    private LocalDateTime orderTime;
    private LocalDate orderDate;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String phoneNo;
    private List<OrderItemResponse> items; // for user side
}
