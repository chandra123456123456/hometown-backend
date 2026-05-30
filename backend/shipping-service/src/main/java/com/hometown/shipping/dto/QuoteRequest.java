package com.hometown.shipping.dto;

import jakarta.validation.constraints.NotBlank;

public record QuoteRequest(
        @NotBlank String pincode,
        int weightGrams,
        int volumeCm3
) {}
