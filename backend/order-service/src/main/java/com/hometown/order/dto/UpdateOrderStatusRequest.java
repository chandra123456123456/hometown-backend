package com.hometown.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull Long orderId,
        @NotBlank String status
) {
}
