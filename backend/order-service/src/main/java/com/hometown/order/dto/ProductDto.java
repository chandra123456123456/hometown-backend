package com.hometown.order.dto;

import java.math.BigDecimal;

public record ProductDto(Long id, BigDecimal price, Long sellerId, Integer stock,
                         Integer weightGrams, Integer lengthCm, Integer widthCm, Integer heightCm) {
}
