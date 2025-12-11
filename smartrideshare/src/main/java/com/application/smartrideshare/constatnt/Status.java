package com.application.smartrideshare.constatnt;

public enum Status {
    SUCCESS("SUCCESS"," Operation completed successfully"),
    FAILURE("FAILURE"," Operation failed"),
    ERROR("ERROR"," An error occurred during the operation"),
    RESET_PASSWORD_REQUIRED("RESET_PASSWORD_REQUIRED"," Password reset is required for first-time login"),
    NONE("NONE"," You are good to go"),
    PASSWORD_RESET_SUCCESS("PASSWORD_RESET_SUCCESS"," Password has been reset successfully"),
    USER_NOT_FOUND("USER_NOT_FOUND"," User not found ... Try entering correct credentials"),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH"," Old password does not match"),
    PASSWORD_SAME_AS_OLD("PASSWORD_SAME_AS_OLD"," New password cannot be the same as the old password"),
    ADMIN_CREATED("ADMIN_CREATED"," Admin user created successfully"),
    INVALID_EMAIL_FORMAT("INVALID_EMAIL_FORMAT"," The provided email format is invalid"),
    DRIVER_CREATED("DRIVER_CREATED"," Driver user created successfully"),
    PASSENGER_CREATED("PASSENGER_CREATED"," Passenger user created successfully"),
    ADMIN_DELETED("ADMIN_SUCCESSFULLY_DELETED","Deletion Complete"),
    ADMIN_NOT_FOUND("ADMIN_NOT_FOUND","User not found ... Try entering correct credentials"),
    DRIVER_DELETED("DRIVER_SUCCESSFULLY_DELETED","Deletion Complete"),
    DRIVER_NOT_FOUND("DRIVER_NOT_FOUND","User not found ... Try entering correct credentials"),
    PASSENGER_DELETED("PASSENGER_SUCCESSFULLY_DELETED","Deletion Complete"),
    PASSENGER_NOT_FOUND("PASSENGER_NOT_FOUND","User not found ... Try) entering correct credentials"),
    LOGOUT_SUCCESS("LOGOUT_SUCCESS"," User logged out successfully"),
    USER_AlREADY_LOGGED_OUT("USER_AlREADY_LOGGED_OUT","User has already been logged out" ),
    RIDE_POSTED_SUCCESS("RIDE_POSTED_SUCCESS"," Ride has been posted successfully"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"," The provided credentials are invalid"),
    ACCOUNT_DELETED("ACCOUNT_DELETED"," Account has been deleted successfully"),
    UNAUTHORIZED("UNAUTHORIZED"," You are not authorized to perform this action"),
    RIDE_NOT_FOUND("RIDE_NOT_FOUND"," The specified ride was not found"),
    RIDE_DELETED("RIDE_DELETED"," Ride has been deleted successfully"),
    NO_SEATS_AVAILABLE("NO_SEATS_AVAILABLE"," No seats are available for this ride"),
    ALREADY_BOOKED("ALREADY_BOOKED"," You have already booked this ride"),
    BOOKING_CONFIRMED("BOOKING_CONFIRMED"," Your booking has been confirmed"),
    BOOKING_NOT_FOUND("BOOKING_NOT_FOUND"," The specified booking was not found"),
    BOOKING_CANCELLED("BOOKING_CANCELLED"," Your booking has been cancelled successfully"),
    ALREADY_CANCELLED("ALREADY_CANCELLED"," This booking has already been cancelled"),
    USER_DELETED("USER_DELETED"," User account has been deleted successfully"),
    INVALID_PICKUP_DROP_POINTS("INVALID_PICKUP_DROP_POINTS","Invalid pickup or dropOff point" ),
    CANCELLED("CANCELLED","Ride is Cancelled" ),
    RESCHEDULED("RESCHEDULED","Ride rescheduled" ),
    COMPLETED("COMPLETED","Ride Completed" );

    ;

    private  String name ;
    private  String description;

    Status(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
