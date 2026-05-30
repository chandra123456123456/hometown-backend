package com.hometown.product.dto;

import java.math.BigDecimal;

public record ProductOrderInfo(
        Long id,
        BigDecimal price,
        Long sellerId,
        Integer stock,
        Integer weightGrams,
        Integer lengthCm,
        Integer widthCm,
        Integer heightCm
) {}
