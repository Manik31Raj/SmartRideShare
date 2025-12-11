package com.application.smartrideshare.dto.request;

import com.application.smartrideshare.model.Role;
import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;
    private Role role;
}
