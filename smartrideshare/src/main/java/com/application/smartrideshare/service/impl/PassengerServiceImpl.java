package com.application.smartrideshare.service.impl;

import com.application.smartrideshare.constatnt.Status;
import com.application.smartrideshare.dto.request.BookingRideRequest;
import com.application.smartrideshare.dto.request.CancelBookingRequest;
import com.application.smartrideshare.dto.response.BookingResponse;
import com.application.smartrideshare.dto.response.RideResponse;
import com.application.smartrideshare.model.*;
import com.application.smartrideshare.repository.BookingRepository;
import com.application.smartrideshare.repository.PassengerRepository;
import com.application.smartrideshare.repository.RideRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl {

    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final AuthServiceImpl authService;


    public List<RideResponse> searchRides(String from, String to) {
        List<Ride> rides = rideRepository
                .findByStartLocationContainingIgnoreCaseAndEndLocationContainingIgnoreCaseAndStatusAndAvailableSeatsGreaterThan(
                        from, to, RideStatus.PENDING, 0);

        return rides.stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Ride> getRideDetailsById(Long rideId) {
        return rideRepository.findById(rideId);
    }


    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String passengerUsername) {
        Passenger passenger = authService.getPassengerByUsername(passengerUsername);
        if (passenger == null) {
            return Collections.emptyList();
        }

        return bookingRepository.findByPassenger(passenger)
                .stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse bookRide(BookingRideRequest request) {
        BookingResponse response = new BookingResponse();

        Passenger passenger = authService.getPassengerByUsername(request.getPassengerUsername());
        Optional<Ride> optionalRide = rideRepository.findById(request.getRideId());

        if (passenger == null) {
            response.setStatus(Status.USER_NOT_FOUND);
            return response;
        }
        if (optionalRide.isEmpty()) {
            response.setStatus(Status.RIDE_NOT_FOUND);
            return response;
        }

        Ride ride = optionalRide.get();

        if (ride.getAvailableSeats() < request.getSeatsToBook()) {
            response.setStatus(Status.NO_SEATS_AVAILABLE);
            return response;
        }

        if (bookingRepository.findByRideAndPassenger(ride, passenger).isPresent()) {
            response.setStatus(Status.ALREADY_BOOKED);
            return response;
        }

        String allStops = ride.getPickupLocation1() + ride.getPickupLocation2() + ride.getPickupLocation3() + ride.getPickupLocation4() + ";" + ride.getDropLocation1() + ride.getDropLocation2() + ride.getDropLocation3() + ride.getDropLocation4() + ";" + ride.getStartLocation() + ";" + ride.getEndLocation();
        if (!allStops.contains(request.getSelectedPickupPoint()) || !allStops.contains(request.getSelectedDropPoint())) {
            response.setStatus(Status.INVALID_PICKUP_DROP_POINTS);
            response.setMessage("Selected points are not valid stops for this route.");
            return response;
        }
        Double bookingFare = ride.getEstimated_fare();
        if (bookingFare == null) {
            throw new IllegalStateException("Ride fare is not set. Cannot book.");
        }

        ride.setAvailableSeats(ride.getAvailableSeats() - request.getSeatsToBook());
        rideRepository.save(ride);

        Booking booking = new Booking();
        booking.setPassenger(passenger);
        booking.setRide(ride);
        booking.setSeatsBooked(request.getSeatsToBook());
        booking.setBookedPickUpPoint(request.getSelectedPickupPoint());
        booking.setBookedDropPoint(request.getSelectedDropPoint());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setFare(bookingFare);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setPaymentMode("COD");
        Booking savedBooking = bookingRepository.save(booking);

        log.info("New booking created: {} for ride {}", savedBooking.getId(), ride.getId());
        response.setStatus(Status.BOOKING_CONFIRMED);
        response.setMessage("Booking confirmed!");
        response.setBookingId(savedBooking.getId());
        return response;
    }

    @Transactional
    public Status cancelBooking(CancelBookingRequest request) {
        Optional<Booking> optionalBooking = bookingRepository.findById(request.getBookingId());

        if (optionalBooking.isEmpty()) {
            return Status.BOOKING_NOT_FOUND;
        }

        Booking booking = optionalBooking.get();

        if (!booking.getPassenger().getUsername().equals(request.getPassengerUsername())) {
            return Status.UNAUTHORIZED;
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return Status.ALREADY_CANCELLED;
        }

        Ride ride = booking.getRide();
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
        rideRepository.save(ride);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        log.info("Booking cancelled: {}", booking.getId());
        return Status.CANCELLED;
    }

    @Transactional
    public Map<String, String> completePayment(Long bookingId, String passengerUsername) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found."));

        // Security check
        if (!booking.getPassenger().getUsername().equals(passengerUsername)) {
            throw new SecurityException("Access denied.");
        }

        // Ensure booking is confirmed before setting paid
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return Map.of("status", "PAYMENT_FAILED_NOT_CONFIRMED");
        }

        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            return Map.of("status", "PAYMENT_ALREADY_COMPLETED");
        }

        // 1. Update Payment Status/Mode
        booking.setPaymentStatus(PaymentStatus.COMPLETED);
        booking.setPaymentMode("PAID (ONLINE)"); // Update to the requested mode

        bookingRepository.save(booking);
        log.info("Payment completed for booking: {}", booking.getId());
        return Map.of("status", "PAYMENT_SUCCESSFUL");
    }

    public Map<String, String> handlePaymentFailure(Long bookingId, String passengerUsername) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found."));
        // Security check
        if (!booking.getPassenger().getUsername().equals(passengerUsername)) {
            throw new SecurityException("Access denied.");
        }
        // Update Payment Status
        booking.setPaymentStatus(PaymentStatus.FAILED);
        bookingRepository.save(booking);
        log.info("Payment failed for booking: {}", booking.getId());
        return Map.of("status", "PAYMENT_FAILED");
    }
}