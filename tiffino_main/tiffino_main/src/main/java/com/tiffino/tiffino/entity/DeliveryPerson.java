package com.tiffino.tiffino.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Getter
@Setter
@Data
@Table(name = "delivery_person")
public class DeliveryPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryPersonId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    private String phoneNo;

    @Column(name = "isAvailable")
    private Boolean isAvailable = true;

    @Column(name = "isActive")
    private Boolean isActive = true;

    @Column(name = "isDeleted")
    private Boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.DELIVERY_PERSON;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "cloud_kitchen_id", nullable = false)
    @JsonIgnoreProperties({"manager", "deliveryPersons", "cloudKitchenMeals", "reviews"})
    private CloudKitchen cloudKitchen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    @JsonIgnoreProperties({"cloudKitchen"})
    private Manager manager;

    @Column(name = "otp")
    private String otp;




}