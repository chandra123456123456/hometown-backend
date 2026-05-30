package com.hometown.order.port;

import com.hometown.order.dto.ProductDto;

public interface PricingPort {
    ProductDto fetchProduct(Long id);
}
