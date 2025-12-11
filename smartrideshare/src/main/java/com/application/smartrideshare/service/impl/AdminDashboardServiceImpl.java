package com.application.smartrideshare.service.impl;

import com.application.smartrideshare.constatnt.Status;
import com.application.smartrideshare.dto.response.*;
import com.application.smartrideshare.model.*;
import com.application.smartrideshare.repository.*;
import com.application.smartrideshare.service.interfaces.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final AdminRepository adminRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final LogsRepository logsRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        stats.setTotalAdmins(adminRepository.count());
        stats.setTotalDrivers(driverRepository.count());
        stats.setTotalPassengers(passengerRepository.count());
        stats.setTotalRides(rideRepository.count());
        stats.setTotalBookings(bookingRepository.count());
        return stats;
    }

    @Override
    public List<AdminResponse> getAllAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(AdminResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(DriverResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PassengerResponse> getAllPassengers() {
        return passengerRepository.findAll()
                .stream()
                .map(PassengerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RideResponse> getAllRides() {
        return rideRepository.findAll()
                .stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Status deleteAdminById(Long id) {
        if (id == null) return Status.ADMIN_NOT_FOUND;
        if (!adminRepository.existsById(id)) return Status.ADMIN_NOT_FOUND;
        adminRepository.deleteById(id);
        return Status.ADMIN_DELETED;
    }

    @Override
    @Transactional
    public Status deleteDriverById(Long driverId) {
        try {
            Driver driver = driverRepository.findById(driverId).orElse(null);
            if (driver == null) {
                return Status.USER_NOT_FOUND;
            }
            List<Ride> rides = rideRepository.findByDriver(driver);
            for (Ride ride : rides) {
                List<Booking> bookings = bookingRepository.findAllByRide(ride);
                bookingRepository.deleteAll(bookings);
            }
            rideRepository.deleteAll(rides);
            logsRepository.deleteByUsername(driver.getUsername());
            driverRepository.delete(driver);
            return Status.USER_DELETED;
        } catch (Exception e) {
            log.error("Error deleting driver {}: {}", driverId, e.getMessage());
            return Status.ERROR;
        }
    }

    @Override
    @Transactional
    public Status deletePassengerById(Long passengerId) {
        try {
            Passenger passenger = passengerRepository.findById(passengerId).orElse(null);
            if (passenger == null) {
                return Status.USER_NOT_FOUND;
            }
            List<Booking> bookings = bookingRepository.findByPassenger(passenger);
            for (Booking booking : bookings) {
                if (booking.getStatus() == BookingStatus.CONFIRMED) {
                    Ride ride = booking.getRide();
                    ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
                    rideRepository.save(ride);
                }
            }
            bookingRepository.deleteAll(bookings);
            logsRepository.deleteByUsername(passenger.getUsername());
            passengerRepository.delete(passenger);
            return Status.USER_DELETED;
        } catch (Exception e) {
            log.error("Error deleting passenger {}: {}", passengerId, e.getMessage());
            return Status.ERROR;
        }
    }

    @Override
    public DriverResponse getDriverById(Long id) {
        if (id == null) return null;
        return driverRepository.findById(id)
                .map(DriverResponse::fromEntity)
                .orElse(null);
    }

    @Override
    public PassengerResponse getPassengerById(Long id) {
        if (id == null) return null;
        return passengerRepository.findById(id)
                .map(PassengerResponse::fromEntity)
                .orElse(null);
    }

    @Override
    public BookingResponse getBookingById(Long id) {
        if (id == null) return null;
        return bookingRepository.findById(id)
                .map(BookingResponse::fromEntity)
                .orElse(null);
    }

    @Override
    public RideResponse getRideById(Long id) {
        if (id == null) return null;
        return rideRepository.findById(id)
                .map(RideResponse::fromEntity)
                .orElse(null);
    }
}
