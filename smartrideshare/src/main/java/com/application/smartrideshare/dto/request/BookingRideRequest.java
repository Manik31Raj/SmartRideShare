package com.application.smartrideshare.dto.request;

import lombok.Data;

@Data
public class BookingRideRequest {
    private Long rideId;
    private String passengerUsername;
    private int seatsToBook = 1;
    private String selectedPickupPoint;
    private String selectedDropPoint;
}
