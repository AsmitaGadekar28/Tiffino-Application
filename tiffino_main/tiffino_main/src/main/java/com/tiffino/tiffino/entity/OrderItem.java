package com.tiffino.tiffino.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long mealId;
    private String mealName;
    private String mealPhoto;
    private double mealPrice;
    private int quantity;



}
