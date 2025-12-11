package com.application.smartrideshare.dto.response;

import com.application.smartrideshare.constatnt.Status;
import com.application.smartrideshare.model.Booking;
import com.application.smartrideshare.model.BookingStatus;
import com.application.smartrideshare.model.PaymentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponse {

    private Status status;
    private String message;

    private Long bookingId;
    private int seatsBooked;
    private BookingStatus bookingStatus;

    private Long rideId;
    private String startLocation;
    private String endLocation;
    private LocalDateTime departureTime;

    private String bookedPickUpPoint;
    private String bookedDropPoint;

    private String driverName;
    private String passengerName;

    private Double fare;
    private String paymentMode;
    private PaymentStatus paymentStatus;

    public static BookingResponse fromEntity(Booking booking) {
        BookingResponse res = new BookingResponse();
        res.setBookingId(booking.getId());
        res.setSeatsBooked(booking.getSeatsBooked());
        res.setBookingStatus(booking.getStatus());
        res.setRideId(booking.getRide().getId());
        res.setStartLocation(booking.getRide().getStartLocation());
        res.setEndLocation(booking.getRide().getEndLocation());
        res.setDepartureTime(booking.getRide().getDepartureTime());
        res.setBookedPickUpPoint(booking.getBookedPickUpPoint());
        res.setBookedDropPoint(booking.getBookedDropPoint());
        res.setFare(booking.getFare());
        res.setPaymentMode(booking.getPaymentMode());
        res.setPaymentStatus(booking.getPaymentStatus());

        if (booking.getPassenger() != null) {
            res.setPassengerName(booking.getPassenger().getFullname());
        } else {
            res.setPassengerName("(N/A)");
        }

        // Mapping Driver Name
        if (booking.getRide().getDriver() != null) {
            res.setDriverName(booking.getRide().getDriver().getFullname());
        } else {
            res.setDriverName("(N/A)");
        }

        return res;
    }
}