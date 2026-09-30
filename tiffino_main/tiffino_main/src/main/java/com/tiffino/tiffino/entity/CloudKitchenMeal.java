package com.tiffino.tiffino.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CloudKitchenMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation with CloudKitchen
    @ManyToOne
    @JoinColumn(name = "cloud_kitchen_id", nullable = false)
    private CloudKitchen cloudKitchen;

    // Relation with Meal
    @ManyToOne
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    // Relation with Manager (new)
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    private boolean available = true; // default true

    // Constructor for creating new mapping
    public CloudKitchenMeal(CloudKitchen cloudKitchen, Meal meal, boolean available, Manager manager) {
        this.cloudKitchen = cloudKitchen;
        this.meal = meal;
        this.available = available;
        this.manager = manager;
    }
}
