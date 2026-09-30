package com.tiffino.tiffino.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    @Enumerated(EnumType.STRING)
    private IssueType issueType;

    private String extraChoice; // e.g. "had bad smell", "flavour"

    private String message; // user typed message

    private String photoUrl; // cloudinary URL

    private String status; // e.g. PENDING, ESCALATED

    private boolean managerNotified;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String userName;       // ✅ add this

    @Column(name = "cloud_kitchen_id")
    private String cloudKitchenId;


    private String phoneNumber;    // ✅ add this

    private String address;        // ✅ add this

    private Long orderId;          // ✅ add this
}
