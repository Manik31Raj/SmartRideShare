package com.application.smartrideshare.controller;


import com.application.smartrideshare.constatnt.Status;
import com.application.smartrideshare.dto.request.CreateAdminRequest;
import com.application.smartrideshare.dto.response.*;
import com.application.smartrideshare.model.Role;
import com.application.smartrideshare.service.impl.AuthServiceImpl;
import com.application.smartrideshare.service.interfaces.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AuthServiceImpl authServiceImpl;
    private final AdminDashboardService adminDashboardService;

    @PostMapping("/register")
    public Response registerNewAdmin(@Validated @RequestBody CreateAdminRequest req) {

        String email = req.getEmail();
        String fullname = req.getFullname();
        String department = req.getDepartment();
        String contactNumber = req.getContactNumber();
        int age = req.getAge();
        Role role = req.getRole();

        String tempPassword = authServiceImpl.AddAdmin(email, fullname, role, department, age, contactNumber, true);
        Response response = new Response();
        if (tempPassword != null) {
            response.setStatus(Status.ADMIN_CREATED);
            response.setTempPassword(tempPassword);
            response.setFullname(fullname);
        } else {
            response.setStatus(Status.ERROR);
        }

        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdminResponse>> getAllAdmins() {
        List<AdminResponse> list = authServiceImpl.getAllAdmins()
                .stream()
                .map(AdminResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public Status deleteAdmin(@PathVariable Long id) {
        boolean deleted = authServiceImpl.deleteAdminById(id);
        if (deleted) {

            return Status.ADMIN_DELETED;
        } else {
            return Status.ADMIN_NOT_FOUND;
        }
    }


    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getDashboardStats());
    }

    @GetMapping("/all-drivers")
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        return ResponseEntity.ok(adminDashboardService.getAllDrivers());
    }

    @GetMapping("/all-passengers")
    public ResponseEntity<List<PassengerResponse>> getAllPassengers() {
        return ResponseEntity.ok(adminDashboardService.getAllPassengers());
    }

    @GetMapping("/all-rides")
    public ResponseEntity<List<RideResponse>> getAllRides() {
        return ResponseEntity.ok(adminDashboardService.getAllRides());
    }

    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(adminDashboardService.getAllBookings());
    }

    @DeleteMapping("/delete-driver/{id}")
    public Status deleteDriver(@PathVariable Long id) {
        log.info("Admin request to delete driver: {}", id);
        return adminDashboardService.deleteDriverById(id);
    }

    @DeleteMapping("/delete-passenger/{id}")
    public Status deletePassenger(@PathVariable Long id) {
        log.info("Admin request to delete passenger: {}", id);
        return adminDashboardService.deletePassengerById(id);
    }

    @GetMapping("/driver/{id}")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable Long id) {
        log.info("Admin request to fetch driver: {}", id);
        DriverResponse driver = adminDashboardService.getDriverById(id);
        if (driver == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/passenger/{id}")
    public ResponseEntity<PassengerResponse> getPassengerById(@PathVariable Long id) {
        log.info("Admin request to fetch passenger: {}", id);
        PassengerResponse passenger = adminDashboardService.getPassengerById(id);
        if (passenger == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(passenger);
    }

    @GetMapping("/Booking/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        log.info("Admin request to fetch booking: {}", id);
        BookingResponse booking = adminDashboardService.getBookingById(id);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(booking);
    }

    @GetMapping("Ride/{id}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable Long id) {
        log.info("Admin request to fetch ride: {}", id);
        RideResponse ride = adminDashboardService.getRideById(id);
        if (ride == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ride);
    }
}
