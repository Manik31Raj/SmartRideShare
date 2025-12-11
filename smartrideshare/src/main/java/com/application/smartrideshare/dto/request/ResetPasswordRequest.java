package com.application.smartrideshare.dto.request;

import com.application.smartrideshare.model.Role;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String username;
    private String oldPassword;
    private String newPassword;
    private Role role;

}
