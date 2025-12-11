package com.application.smartrideshare.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    // --- REVERTED FIELDS ---
    @Column(nullable = false)
    private String startLocation;

    @Column(nullable = false)
    private String endLocation;
    // --- END REVERTED FIELDS ---

    @Column(nullable = true)
    private String pickupLocation1;
    @Column(nullable = true)
    private String pickupLocation2;
    @Column(nullable = true)
    private String pickupLocation3;
    @Column(nullable = true)
    private String pickupLocation4;

    @Column(nullable = true)
    private String dropLocation1;
    @Column(nullable = true)
    private String dropLocation2;
    @Column(nullable = true)
    private String dropLocation3;
    @Column(nullable = true)
    private String dropLocation4;

    private String vehicleModel;
    private String vehiclePlate;

    private Double distance_meters;
    private Double duration_seconds;
    private Double estimated_fare;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private int availableSeats;

    @Enumerated(EnumType.STRING)
    private RideStatus status;
}
