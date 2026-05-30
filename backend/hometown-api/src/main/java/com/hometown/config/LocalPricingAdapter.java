package com.hometown.config;

import com.hometown.order.dto.ProductDto;
import com.hometown.order.port.PricingPort;
import com.hometown.product.dto.ProductResponse;
import com.hometown.product.service.ProductService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!microservices")
public class LocalPricingAdapter implements PricingPort {

    private final ProductService productService;

    public LocalPricingAdapter(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ProductDto fetchProduct(Long id) {
        ProductResponse p = productService.findById(id);
        return new ProductDto(p.id(), p.price(), p.sellerId(), p.stock());
    }
}
