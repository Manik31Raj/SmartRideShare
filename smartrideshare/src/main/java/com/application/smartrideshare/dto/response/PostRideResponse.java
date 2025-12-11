package com.application.smartrideshare.dto.response;

import com.application.smartrideshare.constatnt.Status;
import lombok.Data;

@Data
public class PostRideResponse {
    private Status status;
    private Long rideId;
    private String message;
}
