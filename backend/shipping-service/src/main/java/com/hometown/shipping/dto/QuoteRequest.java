package com.hometown.shipping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record QuoteRequest(
        @NotBlank String pincode,
        @Positive int weightGrams
) {}
