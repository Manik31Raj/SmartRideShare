package com.application.smartrideshare.repository;

import com.application.smartrideshare.model.Booking;
import com.application.smartrideshare.model.Passenger;
import com.application.smartrideshare.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // To show "My Bookings"
    List<Booking> findByPassenger(Passenger passenger);

    // To check if a passenger has already booked a specific ride
    Optional<Booking> findByRideAndPassenger(Ride ride, Passenger passenger);

    List<Booking> findAllByRide(Ride ride);
}
