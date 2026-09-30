package com.tiffino.tiffino.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cuisines")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cuisine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuisine_id")
    private Long cuisineId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "is_unavailable")
    private Boolean isUnavailable = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cuisine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Meal> meals;

    @Column(name = "photo")
    private String photo;

    @Column(name = "state_name")
    private String stateName;

}

