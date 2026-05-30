package com.hometown.config;

import com.hometown.order.dto.ShippingEstimateDto;
import com.hometown.order.port.ShippingPort;
import com.hometown.shipping.provider.ShippingOption;
import com.hometown.shipping.service.ShippingService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("!microservices")
public class LocalShippingAdapter implements ShippingPort {

    private final ShippingService shippingService;

    public LocalShippingAdapter(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @Override
    public ShippingEstimateDto estimate(String pincode, int weight) {
        ShippingOption option = shippingService.estimate(pincode, weight);
        if (option == null) {
            return new ShippingEstimateDto(null, false, 0, BigDecimal.ZERO);
        }
        return new ShippingEstimateDto(option.partner(), option.serviceable(), option.etaDays(), option.charge());
    }
}
