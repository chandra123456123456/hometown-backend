package com.hometown.order.port;

import com.hometown.order.dto.ProductDto;

import java.math.BigDecimal;

public interface PricingPort {
    ProductDto fetchProduct(Long id);

    BigDecimal frameCharge(Long productId, String frameType);
}
