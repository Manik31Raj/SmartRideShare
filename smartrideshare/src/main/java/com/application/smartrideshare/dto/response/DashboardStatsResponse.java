package com.application.smartrideshare.dto.response;

import lombok.Data;

@Data
public class DashboardStatsResponse {
    private long totalAdmins;
    private long totalDrivers;
    private long totalPassengers;
    private long totalRides;
    private long totalBookings;
}