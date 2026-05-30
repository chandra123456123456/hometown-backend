package com.hometown.analytics.dto;

public record SellerSummaryResponse(
        long totalVisits,
        long productViews,
        long addToCarts,
        long checkouts,
        long uniqueSessions
) {}
