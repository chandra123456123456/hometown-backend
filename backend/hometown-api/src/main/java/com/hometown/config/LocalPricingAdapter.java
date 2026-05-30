package com.hometown.config;

import com.hometown.order.dto.ProductDto;
import com.hometown.order.port.PricingPort;
import com.hometown.product.dto.ProductOrderInfo;
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
        ProductOrderInfo p = productService.getOrderInfo(id);
        return new ProductDto(p.id(), p.price(), p.sellerId(), p.stock(),
                p.weightGrams(), p.lengthCm(), p.widthCm(), p.heightCm());
    }
}
