package com.application.smartrideshare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "drivers")
@Inheritance(strategy = InheritanceType.JOINED)  // ✅ keep inheritance hierarchy
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String fullname;
    private Instant createdAt = Instant.now();
    private String contactNumber;

    @Column(nullable = false)
    private boolean isFirstLogin = true;

    private int age;

    private String licenseNumber;
    private String vehicleModel;
    private String vehiclePlate;
}