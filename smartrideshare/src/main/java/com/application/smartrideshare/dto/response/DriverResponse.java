package com.application.smartrideshare.dto.response;

import com.application.smartrideshare.model.Driver;
import com.application.smartrideshare.model.Passenger;
import com.application.smartrideshare.model.Role;
import lombok.Data;

@Data
public class DriverResponse {

    private Long id;
    private String username;
    private String fullname;
    private String email;
    private String contactNumber;
    private Integer age;
    private Role role;
    private String licenseNumber;
    private String vehicleModel;
    private String vehiclePlate;

    public static DriverResponse fromEntity(Driver a) {
        DriverResponse d = new DriverResponse();
        d.setId(a.getId());
        d.setUsername(a.getUsername());
        d.setFullname(a.getFullname());
        d.setEmail(a.getUsername());
        d.setContactNumber(a.getContactNumber());
        d.setAge(a.getAge());
        d.setRole(a.getRole());
        d.setLicenseNumber(a.getLicenseNumber());
        d.setVehicleModel(a.getVehicleModel());
        d.setVehiclePlate(a.getVehiclePlate());
        return d;
    }
}
