package com.tiffino.tiffino.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String durationType;     // DAILY/WEEKLY/MONTHLY
    private String mealTimes;        // CSV of meals
    private String allergies;        // CSV
    private Integer caloriesPerMeal;
    private String dietaryFilePath;

  //  private LocalDate startDate;
    //private LocalDate endDate;
  @Column(name = "is_active", nullable = false)
  private boolean active = false;



    private Double subscriptionPrice;        // computed price before discount
    private double appliedDiscountPercent;   // if any
    private Double finalPrice;               // after discount
    private LocalDateTime subscriptionDate;



    //testing ke liye
    private LocalDateTime startDate; // date + time
    private LocalDateTime endDate;   // date + time

    @ManyToOne
    private User user;

    // 🔹 Test Mode Flag
    private static final boolean TEST_MODE = true;

    // 🔹 Method to set endDate based on durationType
    public void setEndDateByDuration() {
        LocalDateTime today = LocalDateTime.now();

        switch (durationType) {
            case "DAILY":
                this.startDate = today;
                this.endDate = TEST_MODE ? today.plusDays(0) : today.plusDays(1); // test -> today itself
                break;

            case "WEEKLY":
                this.startDate = today;
                this.endDate = TEST_MODE ? today.plusDays(1) : today.plusWeeks(1); // test -> 1 day
                break;

            case "MONTHLY":
                this.startDate = today;
                this.endDate = TEST_MODE ? today.plusDays(1) : today.plusMonths(1); // test -> 1 day
                break;

            case "QUARTERLY":
                this.startDate = today;
                this.endDate = TEST_MODE ? today.plusDays(2) : today.plusMonths(3); // test -> 2 days
                break;

            default:
                throw new IllegalArgumentException("Invalid duration type: " + durationType);
        }
    }
}
