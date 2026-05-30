package com.hometown.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotBlank String name,
        String description,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        @Min(0) @Max(100) int discountPercent,
        @NotNull Long categoryId,
        @Min(0) int stock,
        boolean active,
        Long sellerId,
        List<String> imageUrls,
        boolean antique,
        Integer weightGrams,
        Integer lengthCm,
        Integer widthCm,
        Integer heightCm
) {}
