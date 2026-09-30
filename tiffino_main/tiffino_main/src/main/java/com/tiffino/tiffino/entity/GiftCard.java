package com.tiffino.tiffino.entity;
/**
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GiftCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String giftCardCode;
    private String validForPlan;
    private int discountPercentage;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
**/
// GiftCard.java
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
/**
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftCard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;             // 6-digit numeric
    @Column(name = "discount_percentage", nullable = false)
    private Double discountPercent;

   // private Double discountPercent;
    private String validForPlan;     // DAILY/WEEKLY/MONTHLY
    private String description;      // e.g. "🎁 Welcome Back..."
    private boolean active;
    private LocalDate generatedDate;

    @ManyToOne
    private User user;
}
**/


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;                // unique code
    @Column(name = "discount_percentage")
    private double discountPercent;

   // private double discountPercent;        // discount %
    private String validForPlan;        // DAILY, MONTHLY, etc.
    private String description;         // like "10% off on DAILY plan"
    private boolean active = true;      // true = not used, false = used
    private LocalDate generatedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
