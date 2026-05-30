package com.hometown.analytics.dto;

import com.hometown.analytics.domain.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VisitEventRequest(
        @NotNull EventType type,
        Long productId,
        Long sellerId,
        String category,
        Long userId,
        boolean guest,
        @NotBlank String sessionId,
        String referrer
) {}
