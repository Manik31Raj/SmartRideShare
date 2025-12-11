package com.application.smartrideshare.repository;

import com.application.smartrideshare.model.Driver;
import com.application.smartrideshare.model.Ride;
import com.application.smartrideshare.model.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriver(Driver driver);

    List<Ride> findByStartLocationContainingIgnoreCaseAndEndLocationContainingIgnoreCaseAndStatusAndAvailableSeatsGreaterThan(
            String startLocation, String endLocation, RideStatus status, int seats);
}