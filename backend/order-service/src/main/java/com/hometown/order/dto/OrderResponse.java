package com.hometown.order.dto;

import com.hometown.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        OrderStatus status,
        BigDecimal totalAmount,
        String shippingAddress,
        String destPincode,
        String shippingPartner,
        BigDecimal shippingCost,
        Integer estimatedDeliveryDays,
        Instant createdAt,
        List<OrderItemDto> items
) {
}
