package com.hometown.product.dto;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        Long productId,
        String reviewerName,
        int rating,
        String comment,
        Instant createdAt
) {}
