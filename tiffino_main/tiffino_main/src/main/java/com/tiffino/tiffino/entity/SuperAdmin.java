package com.tiffino.tiffino.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
@Entity
@Table(name = "superadmin")
public class SuperAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "super_admin_id")
    private Long superAdminId;

    @Column(name = "admin_name")
    private String adminName;

    @Column(name = "email", unique = true, nullable = false)
   // @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.SUPER_ADMIN;

    private Date lastPasswordChange = new Date(); // ✅ Add this for token invalidation

}