package com.tiffino.tiffino.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private double totalAmount;

    private String status;

    @CreationTimestamp
    @Column(name = "order_time", nullable = false, updatable = false)
    private LocalDateTime orderTime;

    private String address;
    private String city;
    private String state;
    private String pinCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "cloud_kitchen_id", nullable = false)
    private CloudKitchen cloudKitchen;

    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private DeliveryPerson deliveryPerson;

    @Column(nullable = false)
    private Boolean cancelled = false;

    private LocalDateTime assignedAt;
    private LocalDateTime pickUpAt;
    private LocalDateTime deliveredAt;

    @ElementCollection
    private List<String> allergies;


}
