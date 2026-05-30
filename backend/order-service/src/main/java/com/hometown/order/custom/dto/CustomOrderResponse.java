package com.hometown.order.custom.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CustomOrderResponse(
        Long id,
        Long userId,
        String customerName,
        String customerPhone,
        String type,
        String description,
        String status,
        BigDecimal quotedPrice,
        LocalDate deliveryDate,
        String adminNotes,
        Instant createdAt,
        Instant updatedAt
) {}
