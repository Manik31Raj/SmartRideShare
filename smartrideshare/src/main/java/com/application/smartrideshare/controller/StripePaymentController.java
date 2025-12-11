package com.application.smartrideshare.controller;

import com.application.smartrideshare.dto.request.CreateCheckoutRequest;
import com.application.smartrideshare.model.Booking;
import com.application.smartrideshare.repository.BookingRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/payment")
@CrossOrigin(origins = "http://localhost:8080")
public class StripePaymentController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${client.base.url}")
    private String clientBaseUrl;

    private  final BookingRepository bookingRepository;

    public StripePaymentController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody CreateCheckoutRequest req) throws Exception {
        if (req == null || req.getBookingId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookingId is required");
        }

        Optional<Booking> maybeBooking = bookingRepository.findById(req.getBookingId());
        if (!maybeBooking.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found for id: " + req.getBookingId());
        }

        Booking booking = maybeBooking.get();
        long amountInPaise;
        if (req.getAmountInPaise() != null && req.getAmountInPaise() > 0) {
            amountInPaise = req.getAmountInPaise();
        } else {
            Double fare = booking.getFare();
            if (fare == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking fare is missing");
            }
            amountInPaise = Math.round(fare * 100);
        }

        // Initialize Stripe
        Stripe.apiKey = stripeApiKey;

        String successUrl = clientBaseUrl + "/payment-success.html?bookingId=" + booking.getId() + "&amount=" + booking.getFare();
        String cancelUrl = clientBaseUrl + "/payment-failure.html?bookingId=" + booking.getId();

        // Create line item product title; include ride id if present
        String productName = "Booking #" + booking.getId();
        if (booking.getRide() != null) {
            productName = "Ride #" + (booking.getRide().getId() != null ? booking.getRide().getId() : booking.getId());
        }

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productName)
                        .addImage("https://img.freepik.com/free-vector/taxi-app-concept_23-2148496627.jpg?semt=ais_items_boosted&w=740")
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("inr")
                        .setUnitAmount(amountInPaise)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(lineItem)
                .build();

        Session session = Session.create(params);

        Map<String, String> response = new HashMap<>();
        response.put("url", session.getUrl());
        return ResponseEntity.ok(response);
    }
}