package com.hometown.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty @Valid List<OrderLineRequest> items,
        @NotBlank String shippingAddress,
        @NotBlank String destPincode,
        String shippingPartner
) {
}
