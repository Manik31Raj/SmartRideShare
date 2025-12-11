package com.application.smartrideshare.dto.response;

import com.application.smartrideshare.model.Ride;
import com.application.smartrideshare.model.RideStatus;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class RideResponse {
    private Long id;
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

    private LocalDateTime departureTime;
    private int availableSeats;
    private RideStatus status;

    private String driverName;

    public static RideResponse fromEntity(Ride ride) {
        RideResponse res = new RideResponse();
        res.setId(ride.getId());
        res.setStartLocation(ride.getStartLocation());
        res.setEndLocation(ride.getEndLocation());

        res.setPickupLocation1(ride.getPickupLocation1());
        res.setPickupLocation2(ride.getPickupLocation2());
        res.setPickupLocation3(ride.getPickupLocation3());
        res.setPickupLocation4(ride.getPickupLocation4());

        res.setDropLocation1(ride.getDropLocation1());
        res.setDropLocation2(ride.getDropLocation2());
        res.setDropLocation3(ride.getDropLocation3());
        res.setDropLocation4(ride.getDropLocation4());


        res.setDepartureTime(ride.getDepartureTime());
        res.setAvailableSeats(ride.getAvailableSeats());
        ride.setDistance_meters(ride.getDistance_meters());
        ride.setDuration_seconds(ride.getDuration_seconds());
        ride.setEstimated_fare(ride.getEstimated_fare());
        res.setStatus(ride.getStatus());

        if (ride.getDriver() != null) {
            res.setDriverName(ride.getDriver().getFullname());
        } else {
            res.setDriverName("(N/A)");
        }

        return res;
    }
}
