package com.application.smartrideshare.controller;


import com.application.smartrideshare.constatnt.Status;

import com.application.smartrideshare.dto.request.CreateDriverRequest;
import com.application.smartrideshare.dto.request.DeleteAccountRequest;
import com.application.smartrideshare.dto.request.PostRideRequest;
import com.application.smartrideshare.dto.response.DriverResponse;
import com.application.smartrideshare.dto.response.PostRideResponse;
import com.application.smartrideshare.dto.response.Response;
import com.application.smartrideshare.dto.response.RideResponse;
import com.application.smartrideshare.model.Driver;
import com.application.smartrideshare.model.Role;
import com.application.smartrideshare.service.impl.AuthServiceImpl;
import com.application.smartrideshare.service.impl.DriverServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/driver")
@CrossOrigin(origins = "*")
public class DriverController {
    private final AuthServiceImpl authServiceImpl;
    private final DriverServiceImpl driverService;

    @PostMapping("/register")
    public Response registerNewDriver(@Validated @RequestBody CreateDriverRequest req) {

        String email = req.getEmail();
        String fullname = req.getFullname();
        String department = req.getDepartment();
        String contactNumber = req.getContactNumber();
        String licenseNumber = req.getLicenseNumber();
        String vehicleModel = req.getVehicleModel();
        String vehiclePlate = req.getVehiclePlate();
        int age = req.getAge();
        Role role = req.getRole();

        String tempPassword = authServiceImpl.AddDriver(email, fullname,role,department,age,contactNumber,licenseNumber,vehicleModel,vehiclePlate,true);
        Response response = new Response();
        if (tempPassword != null) {
            response.setStatus(Status.DRIVER_CREATED);
            response.setTempPassword(tempPassword);
            response.setFullname(fullname);
        } else {
            response.setStatus(Status.ERROR);
        }

        log.info("response: {}", response);
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<List<DriverResponse>> getAllAdmins() {
        List<DriverResponse> list = authServiceImpl.getAllDrivers()
                .stream()
                .map(DriverResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public Status deleteDriver(@PathVariable Long id) {
        boolean deleted = authServiceImpl.deleteDriverById(id);
        if (deleted) {
            log.info("Driver with id {} deleted successfully.", id);
            return Status.DRIVER_DELETED;
        } else {
            log.info("Driver with id {} not found.", id);
            return Status.DRIVER_NOT_FOUND;
        }
    }

    @PostMapping("/ride/post")
    public PostRideResponse postRide(@RequestBody PostRideRequest request) {
        log.info("Received request to post ride for driver: {}", request.getDriverUsername());
        return driverService.createRide(request);
    }

    @DeleteMapping("/account/delete")
    public Status deleteAccount(@RequestBody DeleteAccountRequest request) {
        log.info("Received request to delete account for: {}", request.getUsername());
        return driverService.deleteDriverAccount(request);
    }

    @DeleteMapping("/ride/delete/{rideId}")
    public Status deleteRide(@PathVariable Long rideId,
                             @RequestParam String driverUsername) {

        log.info("Received request to delete ride {} by user {}", rideId, driverUsername);
        return driverService.deleteRide(rideId, driverUsername);
    }

    @GetMapping("/rides/{driverUsername}")
    public List<RideResponse> getMyRides(@PathVariable String driverUsername) {
        return driverService.getRidesByDriver(driverUsername);
    }

    @GetMapping("/details/{username}")
    public ResponseEntity<Driver> getDriverDetails(@PathVariable String username) {
        Optional<Driver> driver = driverService.getDriverVehicleDetails(username);
        log.info("Fetched vehicle details for driver: {}", username);
        return driver.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/ride/cancel/{rideId}")
    public Status cancelRide(@PathVariable Long rideId,
                             @RequestBody String driverUsername) {
        log.info("Received request to cancel ride {} by user {}", rideId, driverUsername);
        return driverService.cancelRide(rideId, driverUsername);
    }

    @PatchMapping("ride/reschedule/{rideId}")
    public Status rescheduleRide(@PathVariable Long rideId,
                             @RequestBody Map<String, String> request) {
        String driverUsername = request.get("driverUsername");
        LocalDateTime departureTime = LocalDateTime.parse(request.get("departureTime"));
        log.info("Received request to reschedule ride {} by user {}", rideId, driverUsername);
        return driverService.rescheduleRide(rideId, driverUsername, departureTime);
    }

    @PatchMapping("/ride/completed/{rideId}")
    public Status completedRide(@PathVariable Long rideId,
                             @RequestBody String driverUsername) {
        log.info("Received request to complete ride {} by user {}", rideId, driverUsername);
        return driverService.completedRide(rideId, driverUsername);
    }

}
