package com.application.smartrideshare.dto.request;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostRideRequest {
    private String driverUsername;
    private String startLocation;
    private String endLocation;

    private String pickupLocation1;
    private String pickupLocation2;
    private String pickupLocation3;
    private String pickupLocation4;

    private String dropLocation1;
    private String dropLocation2;
    private String dropLocation3;
    private String dropLocation4;

    private String vehicleModel;
    private String vehiclePlate;

    private LocalDateTime departureTime;
    private int availableSeats;

    private Double distance_meters;
    private Double duration_seconds;
    private Double estimated_fare;
}
