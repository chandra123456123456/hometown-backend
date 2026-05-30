package com.hometown.order.dto;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long productId,
        int quantity,
        BigDecimal price,
        Long sellerId,
        String frameType,
        BigDecimal frameCharge
) {
}
