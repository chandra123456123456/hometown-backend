package com.hometown.payment.dto;

import com.hometown.payment.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentStatus status,
        String mockTransactionId,
        Instant createdAt
) {
}
