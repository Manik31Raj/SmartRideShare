package com.application.smartrideshare.dto.response;

import com.application.smartrideshare.model.Admin;
import com.application.smartrideshare.model.Passenger;
import com.application.smartrideshare.model.Role;
import lombok.Data;

@Data
public class PassengerResponse {

    private Long id;
    private String username;
    private String fullname;
    private String email;
    private String contactNumber;
    private Integer age;
    private Role role;

    public static PassengerResponse fromEntity(Passenger a) {
        PassengerResponse p = new PassengerResponse();
        p.setId(a.getId());
        p.setUsername(a.getUsername());
        p.setFullname(a.getFullname());
        p.setEmail(a.getUsername());
        p.setContactNumber(a.getContactNumber());
        p.setAge(a.getAge());
        p.setRole(a.getRole());
        return p;
    }
}
