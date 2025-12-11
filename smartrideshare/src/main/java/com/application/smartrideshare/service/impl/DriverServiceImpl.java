package com.application.smartrideshare.service.impl;

import com.application.smartrideshare.constatnt.Status;
import com.application.smartrideshare.dto.request.DeleteAccountRequest;
import com.application.smartrideshare.dto.request.PostRideRequest;
import com.application.smartrideshare.dto.response.PostRideResponse;
import com.application.smartrideshare.dto.response.RideResponse;
import com.application.smartrideshare.model.Driver;
import com.application.smartrideshare.model.Ride;
import com.application.smartrideshare.model.RideStatus;
import com.application.smartrideshare.model.Role;
import com.application.smartrideshare.repository.DriverRepository;
import com.application.smartrideshare.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final AuthServiceImpl authService;
    private final EmailService emailService;

    @Transactional
    public PostRideResponse createRide(PostRideRequest request) {
        PostRideResponse response = new PostRideResponse();

        Driver driver = authService.getDriverByUsername(request.getDriverUsername());

        if (driver == null) {
            response.setStatus(Status.USER_NOT_FOUND);
            response.setMessage("Driver not found");
            return response;
        }

        try {
            Ride ride = new Ride();
            log.info("Creating ride: {}", ride);
            ride.setDriver(driver);
            ride.setStartLocation(request.getStartLocation());
            ride.setEndLocation(request.getEndLocation());
            ride.setPickupLocation1(request.getPickupLocation1());
            ride.setPickupLocation2(request.getPickupLocation2());
            ride.setPickupLocation3(request.getPickupLocation3());
            ride.setPickupLocation4(request.getPickupLocation4());
            ride.setDropLocation1(request.getDropLocation1());
            ride.setDropLocation2(request.getDropLocation2());
            ride.setDropLocation3(request.getDropLocation3());
            ride.setDropLocation4(request.getDropLocation4());
            ride.setVehicleModel(request.getVehicleModel());
            ride.setVehiclePlate(request.getVehiclePlate());
            ride.setDepartureTime(request.getDepartureTime());
            ride.setAvailableSeats(request.getAvailableSeats());
            ride.setDistance_meters(request.getDistance_meters());
            ride.setDuration_seconds(request.getDuration_seconds());
            ride.setEstimated_fare(request.getEstimated_fare());
            log.info("Estimated fare set to: {}", request.getEstimated_fare());
            ride.setStatus(RideStatus.PENDING);
            Ride savedRide = rideRepository.save(ride);
            log.info("Ride created successfully: {}", savedRide);

        } catch (Exception e) {
            log.error("Detailed Exception thrown during ride posting: ", e);
            response.setStatus(Status.ERROR);
            response.setMessage("An error occurred while posting the ride.");
        }
        response.setStatus(Status.RIDE_POSTED_SUCCESS);
        response.setMessage("Ride posted successfully.");
        log.info("Response : {}",response);

        try {


            String subject = "Ride Posted Successfully";
            String username = driver.getUsername();
            String DriverUserName = driver.getFullname();
            String body = "Dear " + DriverUserName + "\n\n" +
                    "Your ride from"  + request.getStartLocation() + " to " + request.getEndLocation() +
                    " has been posted successfully.\n" +
                    "Departure Time: " + request.getDepartureTime() + "\n" +
                    "Available Seats: " + request.getAvailableSeats() + "\n" +
                    "Estimated Fare: $" + request.getEstimated_fare() + "\n\n" +
                    "Thank you for using our Smart Ride Sharing Service!\n\n" +
                    "Best regards,\n" +
                    "Smart Ride Sharing Team";

            emailService.sendSimpleEmail(
                    username,
                    subject,
                    body
            );
            log.info("Email sent successfully!");
        } catch (Exception e) {
            log.info("Error sending email: " + e.getMessage());
        }

        return response;
    }

    @Transactional
    public Status deleteDriverAccount(DeleteAccountRequest request) {
        // 1. Verify credentials first
        boolean valid = authService.verifyCredentials(request.getUsername(), request.getPassword(), Role.DRIVER);

        if (!valid) {
            return Status.INVALID_CREDENTIALS;
        }

        try {
            // 2. Get the driver
            Driver driver = authService.getDriverByUsername(request.getUsername());
            if (driver == null) {
                return Status.USER_NOT_FOUND;
            }

            // 3. Delete the driver (Note: Deletion of related data should be handled here or via DB cascade)
            driverRepository.delete(driver);

            log.info("Driver account deleted: {}", request.getUsername());
            return Status.ACCOUNT_DELETED;

        } catch (Exception e) {
            log.error("Error deleting account: {}", e.getMessage());
            return Status.ERROR;
        }
    }

    @Transactional
    public Status deleteRide(Long rideId, String driverUsername) {
        // 1. Find the ride
        Optional<Ride> optionalRide = rideRepository.findById(rideId);
        if (optionalRide.isEmpty()) {
            return Status.RIDE_NOT_FOUND;
        }

        Ride ride = optionalRide.get();

        // 2. Check if the driver who posted the ride is the one deleting it
        if (!ride.getDriver().getUsername().equals(driverUsername)) {
            log.warn("Unauthorized attempt to delete ride {} by user {}", rideId, driverUsername);
            return Status.UNAUTHORIZED;
        }

        // 3. Delete the ride
        try {
            rideRepository.delete(ride);
            log.info("Ride deleted successfully: {}", rideId);
            return Status.RIDE_DELETED;
        } catch (Exception e) {
            log.error("Error deleting ride {}: {}", rideId, e.getMessage());
            return Status.ERROR;
        }
    }

    public List<RideResponse> getRidesByDriver(String driverUsername) {
        Driver driver = authService.getDriverByUsername(driverUsername);
        if (driver == null) {
            log.warn("Attempt to get rides for non-existent driver: {}", driverUsername);
            return Collections.emptyList();
        }

        return rideRepository.findByDriver(driver)
                .stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<Driver> getDriverVehicleDetails(String username) {
        log.info("Fetching vehicle details for driver: {}", username);
        return driverRepository.findByUsername(username);
    }

    @Transactional
    public Status cancelRide(Long rideId, String driverUsername) {
        Optional<Ride> optionalRide = rideRepository.findById(rideId);
        if (optionalRide.isEmpty()) {
            return Status.RIDE_NOT_FOUND;
        }

        Ride ride = optionalRide.get();

//        if (!ride.getDriver().getUsername().equals(driverUsername)) {
//            log.warn("Unauthorized attempt to cancel ride {} by user {}", rideId, driverUsername);
//            return Status.UNAUTHORIZED;
//        }

        if (ride.getStatus() == RideStatus.CANCELLED) {
            log.info("Ride {} is already cancelled", rideId);
            return Status.ERROR;
        }

        try {
            ride.setStatus(RideStatus.CANCELLED);
            rideRepository.save(ride);
            log.info("Ride cancelled successfully: {}", rideId);
            return Status.CANCELLED;

        } catch (Exception e) {
            log.error("Error cancelling ride {}: {}", rideId, e.getMessage());
            return Status.ERROR;
        }
    }

    @Transactional
    public Status rescheduleRide(Long rideId, String driverUsername, LocalDateTime departureTime) {
        Optional<Ride> optionalRide = rideRepository.findById(rideId);
        if (optionalRide.isEmpty()) {
            return Status.RIDE_NOT_FOUND;
        }

        Ride ride = optionalRide.get();

        if (!ride.getDriver().getUsername().equals(driverUsername)) {
            log.warn("Unauthorized attempt to reschedule ride {} by user {}", rideId, driverUsername);
            return Status.UNAUTHORIZED;
        }

        try {
            ride.setDepartureTime(departureTime);
            rideRepository.save(ride);
            log.info("Ride rescheduled successfully: {}", rideId);
            return Status.RESCHEDULED;

        } catch (Exception e) {
            log.error("Error rescheduling ride {}: {}", rideId, e.getMessage());
            return Status.ERROR;
        }
    }

    public Status completedRide(Long rideId, String driverUsername) {
        Optional<Ride> optionalRide = rideRepository.findById(rideId);
        if (optionalRide.isEmpty()) {
            return Status.RIDE_NOT_FOUND;
        }

        Ride ride = optionalRide.get();

//        if (!ride.getDriver().getUsername().equals(driverUsername)) {
//            log.warn("Unauthorized attempt to complete ride {} by user {}", rideId, driverUsername);
//            return Status.UNAUTHORIZED;
//        }

        try {
            ride.setStatus(RideStatus.COMPLETED);
            rideRepository.save(ride);
            log.info("Ride marked as completed successfully: {}", rideId);
            return Status.COMPLETED;

        } catch (Exception e) {
            log.error("Error marking ride {} as completed: {}", rideId, e.getMessage());
            return Status.ERROR;
        }
    }
}
