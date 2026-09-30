package com.tiffino.tiffino.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "manager", "cloudKitchenMeals", "reviews"})

@Table(name = "cloud_kitchen")
public class CloudKitchen {

    @Id
    @Column(name = "cloud_kitchen_id")
    private String cloudKitchenId;

    @Column(name = "state")
    private String state;

    @Column(name = "city")
    private String city;

    @Column(name = "division")
    private String division;

    @Column(name = "address")
    private String address;

    @Column(name = "pin_code")
    private Integer pinCode;

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
/**
    @OneToOne(mappedBy = "cloudKitchen")
    private Manager manager;**/

    @OneToOne(mappedBy = "cloudKitchen", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"cloudKitchen"})
    private Manager manager;

    @Column(name = "name", nullable = true)
    private String name;

    @OneToMany(mappedBy = "cloudKitchen", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"cloudKitchen"})
    private List<CloudKitchenMeal> cloudKitchenMeals;

    @Column(name = "is_open")
    private Boolean isOpen = false;


    //New
    @OneToMany(mappedBy = "cloudKitchen", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"cloudKitchen"})
    private List<Review> reviews;


}