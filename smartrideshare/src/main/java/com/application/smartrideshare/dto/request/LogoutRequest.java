package com.application.smartrideshare.dto.request;

import com.application.smartrideshare.model.Role;
import lombok.Data;

@Data
public class LogoutRequest {
    private String username;
    private Role role;
}
