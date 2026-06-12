package com.json.AutoAlquiler.dtos;

import java.math.BigDecimal;

public record DashboardMetricsDTO(
    long activeVehicles,
    long reservationsToday,
    BigDecimal monthlyRevenue,
    double occupancyRate,
    BigDecimal dailyRevenue,
    BigDecimal yearlyRevenue
) {}