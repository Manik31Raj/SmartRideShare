package com.application.smartrideshare.dto.request;

import lombok.Data;

@Data
public class CreateCheckoutRequest {
    private Long bookingId;
    // optional override amount (in case you want to pass a custom amount in paise)
    private Long amountInPaise;
}