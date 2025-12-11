package com.application.smartrideshare.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class CancelBookingRequest {

    @JsonProperty("bookingId")
    private Long bookingId;

    @JsonProperty("passengerUsername")
    private String passengerUsername;

}
