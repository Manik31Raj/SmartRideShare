package com.application.smartrideshare.dto.request;


import com.application.smartrideshare.model.Role;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class CreateDriverRequest {


    private String fullname;

    @Email(message = "Invalid email format")
    private String email;
    private String department;
    private String contactNumber;
    private  int age;

    private String licenseNumber;
    private String vehicleModel;
    private String vehiclePlate;

    private Role role;


}
