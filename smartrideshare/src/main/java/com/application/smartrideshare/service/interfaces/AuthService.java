package com.application.smartrideshare.service.interfaces;

import com.application.smartrideshare.model.Admin;
import com.application.smartrideshare.model.Driver;
import com.application.smartrideshare.model.Passenger;

import java.util.List;

public interface AuthService {
    List<Admin> getAllAdmins();
    List<Passenger> getAllPassengers();
    List<Driver> getAllDrivers();
}