package com.tiffino.tiffino.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cloudKitchen"})

@Data
@Getter
@Setter
@Table(name = "manager")
public class Manager {

    @Id
    @Column(name = "manager_id")
    private String managerId;

    @Column(name = "manager_name")
    private String managerName;

    @Column(name = "manager_email", unique = true)
    private String managerEmail;

    @Column(name = "dob")
    private String dob;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "permeant_address")
    private String permeantAddress;

    @Column(name = "adhar_card")
    private String adharCard;

    @Column(name = "pan_card")
    private String panCard;

    @Column(name = "photo")
    private String photo;

    @Column(name = "password")
    private String password;

    @Column(name = "city")
    private String city;

    @Column(name = "isActive")
    private Boolean isActive = true;

    @Column(name = "isDeleted")
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.MANAGER;
/**
    @OneToOne
    @JoinColumn(name = "cloud_kitchen_id")
    private CloudKitchen cloudKitchen;**/

    @OneToOne(fetch = FetchType.LAZY)  // prevents duplicate fetch problem
    @JoinColumn(name = "cloud_kitchen_id", unique = true)
    @JsonIgnoreProperties({"manager", "deliveryPersons", "cloudKitchenMeals", "reviews"})
    private CloudKitchen cloudKitchen;

    @Column(name = "otp")
    private String otp;




}
