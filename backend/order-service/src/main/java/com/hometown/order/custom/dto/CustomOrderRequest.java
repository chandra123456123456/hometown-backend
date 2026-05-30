package com.hometown.order.custom.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomOrderRequest(
        @NotBlank String type,
        @NotBlank String description,
        @NotBlank String customerName,
        @NotBlank String customerPhone
) {}
