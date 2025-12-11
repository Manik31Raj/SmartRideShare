package com.application.smartrideshare.controller;


import com.application.smartrideshare.constatnt.Status;
import com.application.smartrideshare.dto.request.BookingActionRequest;
import com.application.smartrideshare.dto.request.BookingRideRequest;
import com.application.smartrideshare.dto.request.CancelBookingRequest;
import com.application.smartrideshare.dto.request.CreatePassengerRequest;
import com.application.smartrideshare.dto.response.BookingResponse;
import com.application.smartrideshare.dto.response.PassengerResponse;
import com.application.smartrideshare.dto.response.Response;
import com.application.smartrideshare.dto.response.RideResponse;
import com.application.smartrideshare.model.Ride;
import com.application.smartrideshare.model.Role;
import com.application.smartrideshare.service.impl.AuthServiceImpl;
import com.application.smartrideshare.service.impl.PassengerServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/passenger")
@CrossOrigin(origins = "*")
public class PassengerController {

    private final AuthServiceImpl authServiceImpl;
    private final PassengerServiceImpl passengerService;

    @PostMapping("/register")
    public Response registerNewPassenger(@Validated @RequestBody CreatePassengerRequest req) {

        String email = req.getEmail();
        String fullname = req.getFullname();
        String contactNumber = req.getContactNumber();
        int age = req.getAge();
        Role role = req.getRole();

        String tempPassword = authServiceImpl.AddPassenger(email, fullname,role,age,contactNumber,true);
        Response response = new Response();
        if (tempPassword != null) {
            response.setStatus(Status.PASSENGER_CREATED);
            response.setTempPassword(tempPassword);
            response.setFullname(fullname);
        } else {
            response.setStatus(Status.ERROR);
        }

        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<List<PassengerResponse>> getAllAdmins() {
        List<PassengerResponse> list = authServiceImpl.getAllPassengers()
                .stream()
                .map(PassengerResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public Status deletePassenger(@PathVariable Long id) {
        boolean deleted = authServiceImpl.deletePassengerById(id);
        if (deleted) {
            return Status.PASSENGER_DELETED;
        } else {
            return Status.PASSENGER_NOT_FOUND;
        }
    }

    @GetMapping("/rides/search")
    public List<RideResponse> searchRides(@RequestParam String from, @RequestParam String to) {
        log.info("Searching for rides from: {} to: {}", from, to);
        return passengerService.searchRides(from, to);
    }

    @GetMapping("/bookings/{passengerUsername}")
    public List<BookingResponse> getMyBookings(@PathVariable String passengerUsername) {
        return passengerService.getMyBookings(passengerUsername);
    }

    @PostMapping("/rides/book")
    public BookingResponse bookRide(@RequestBody BookingRideRequest request) {
        log.info("Booking request for ride: {} by user: {}", request.getRideId(), request.getPassengerUsername());
        return passengerService.bookRide(request);
    }

    @PatchMapping("/bookings/cancel")
    public Status cancelBooking(@RequestBody CancelBookingRequest request) {
        log.info("Cancellation request for booking: {}", request.getBookingId());
        return passengerService.cancelBooking(request);
    }

    @PatchMapping("/payment-success/{bookingId}")
    public ResponseEntity<Map<String, String>> paymentSuccess(
            @PathVariable Long bookingId,
            @RequestParam String passengerUsername) { // Request body contains passengerUsername

        Map<String, String> response = passengerService.completePayment(bookingId, passengerUsername);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/payment-failure/{bookingId}")
    public ResponseEntity<Map<String, String>> paymentFailure(
            @PathVariable Long bookingId,
            @RequestParam String passengerUsername) { // Request body contains passengerUsername

        Map<String, String> response = passengerService.handlePaymentFailure(bookingId, passengerUsername);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ride-details/{rideId}")
    public ResponseEntity<Ride> getRideDetails(@PathVariable Long rideId) {
        Optional<Ride> ride = passengerService.getRideDetailsById(rideId);
        log.info("Fetching details for ride ID: {}", rideId);
        return ride.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
