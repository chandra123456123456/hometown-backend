package com.hometown.order.port;

import com.hometown.order.client.ProductClient;
import com.hometown.order.dto.ProductDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("microservices")
public class FeignPricingAdapter implements PricingPort {

    private final ProductClient productClient;

    public FeignPricingAdapter(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Override
    public ProductDto fetchProduct(Long id) {
        return productClient.getProduct(id);
    }
}
