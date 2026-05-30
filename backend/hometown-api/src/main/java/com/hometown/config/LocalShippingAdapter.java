package com.hometown.config;

import com.hometown.order.dto.ShippingEstimateDto;
import com.hometown.order.port.ShippingPort;
import com.hometown.shipping.provider.Parcel;
import com.hometown.shipping.provider.ShippingOption;
import com.hometown.shipping.service.ShippingService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("!microservices")
public class LocalShippingAdapter implements ShippingPort {

    private final ShippingService shippingService;

    public LocalShippingAdapter(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @Override
    public ShippingEstimateDto estimate(String pincode, int weightGrams, int volumeCm3) {
        ShippingOption option = shippingService.estimate(pincode, new Parcel(weightGrams, volumeCm3));
        if (option == null) {
            return new ShippingEstimateDto(null, false, 0, BigDecimal.ZERO);
        }
        return map(option);
    }

    @Override
    public List<ShippingEstimateDto> quotes(String pincode, int weightGrams, int volumeCm3) {
        return shippingService.quotes(pincode, new Parcel(weightGrams, volumeCm3)).stream()
                .map(this::map)
                .toList();
    }

    private ShippingEstimateDto map(ShippingOption o) {
        return new ShippingEstimateDto(o.partner(), o.serviceable(), o.etaDays(), o.charge());
    }
}
