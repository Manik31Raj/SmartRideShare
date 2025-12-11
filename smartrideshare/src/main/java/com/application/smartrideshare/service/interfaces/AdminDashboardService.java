package com.application.smartrideshare.service.interfaces;

import com.application.smartrideshare.constatnt.Status;
import com.application.smartrideshare.dto.response.*;

import java.util.List;

public interface AdminDashboardService {

    DashboardStatsResponse getDashboardStats();

    List<AdminResponse> getAllAdmins();
    List<DriverResponse> getAllDrivers();
    List<PassengerResponse> getAllPassengers();
    List<RideResponse> getAllRides();
    List<BookingResponse> getAllBookings();

    Status deleteAdminById(Long id);
    Status deleteDriverById(Long id);
    Status deletePassengerById(Long id);
    DriverResponse getDriverById(Long id);

    PassengerResponse getPassengerById(Long id);

    BookingResponse getBookingById(Long id);

    RideResponse getRideById(Long id);
}