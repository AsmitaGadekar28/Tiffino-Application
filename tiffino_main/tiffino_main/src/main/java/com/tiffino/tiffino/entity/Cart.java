package com.tiffino.tiffino.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User owning the cart
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Cloud kitchen id (string like PUNKAT003)
    @Column(name = "cloud_kitchen_id", nullable = false)
    private String cloudKitchenId;

    // If you want to store snapshot of name, you can
    @Column(name = "cloud_kitchen_city_division")
    private String cloudKitchenName; // city-division

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection
    private List<String> allergies = new ArrayList<>();

    private double totalAmount;
}
