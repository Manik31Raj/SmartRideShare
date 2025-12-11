package com.application.smartrideshare.dto.response;

import com.application.smartrideshare.constatnt.Status;
import com.application.smartrideshare.model.Role;
import lombok.Data;

@Data
public class LoginResponse {
    private String username;
    private String fullname;
    private boolean isFirstLogin;
    private Role role;
    private Status nextStep;
    private Status status;

}
