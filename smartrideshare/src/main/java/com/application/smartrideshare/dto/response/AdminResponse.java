package com.application.smartrideshare.dto.response;

import com.application.smartrideshare.model.Admin;
import com.application.smartrideshare.model.Role;
import lombok.Data;

@Data
public class AdminResponse {

    private Long id;
    private String username;
    private String fullname;
    private String email;
    private String contactNumber;
    private String department;
    private Integer age;
    private Role role;

    public static AdminResponse fromEntity(Admin a) {
        AdminResponse r = new AdminResponse();
        r.setId(a.getId());
        r.setUsername(a.getUsername());
        r.setFullname(a.getFullname());
        r.setEmail(a.getUsername());
        r.setContactNumber(a.getContactNumber());
        r.setDepartment(a.getDepartment());
        r.setAge(a.getAge());
        r.setRole(a.getRole());
        return r;
    }
}
