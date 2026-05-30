package com.hometown.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ShippingQuoteRequest(@NotEmpty List<OrderLineRequest> items, @NotBlank String pincode) {
}
