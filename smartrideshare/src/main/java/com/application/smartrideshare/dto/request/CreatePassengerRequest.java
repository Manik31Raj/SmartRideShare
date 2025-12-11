package com.application.smartrideshare.dto.request;


import com.application.smartrideshare.model.Role;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class CreatePassengerRequest {


    private String fullname;

    @Email(message = "Invalid email format")
    private String email;
    private String contactNumber;
    private  int age;

    private Role role;

}
