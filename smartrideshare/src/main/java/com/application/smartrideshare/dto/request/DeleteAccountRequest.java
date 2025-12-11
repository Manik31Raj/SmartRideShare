package com.application.smartrideshare.dto.request;

import lombok.Data;

@Data
public class DeleteAccountRequest {
    private String username;
    private String password; // Good practice to confirm deletion with a password
}