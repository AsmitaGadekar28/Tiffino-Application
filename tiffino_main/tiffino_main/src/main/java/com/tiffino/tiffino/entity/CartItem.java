package com.tiffino.tiffino.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many items belong to one cart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "meal_id", nullable = false)
    private Long mealId;

    @Column(name = "meal_name")
    private String mealName;

    @Column(name = "meal_photo")
    private String mealPhoto;

    // store the original meal price at time of adding
    @Column(name = "meal_base_price")
    private Double mealBasePrice;

    @Column(name = "quantity")
    private Integer quantity = 1;

    /**@ManyToOne
    @JoinColumn(name = "cloud_kitchen_id")
    private CloudKitchen cloudKitchen;**/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloud_kitchen_id", nullable = false)
    private CloudKitchen cloudKitchen;




}

