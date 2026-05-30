package com.hometown.order.port;

import com.hometown.order.client.ShippingClient;
import com.hometown.order.dto.ShippingEstimateDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("microservices")
public class FeignShippingAdapter implements ShippingPort {

    private final ShippingClient shippingClient;

    public FeignShippingAdapter(ShippingClient shippingClient) {
        this.shippingClient = shippingClient;
    }

    @Override
    public ShippingEstimateDto estimate(String pincode, int weightGrams, int volumeCm3) {
        return shippingClient.getEstimate(pincode, weightGrams, volumeCm3);
    }

    @Override
    public List<ShippingEstimateDto> quotes(String pincode, int weightGrams, int volumeCm3) {
        return shippingClient.getQuotes(new ShippingClient.QuoteBody(pincode, weightGrams, volumeCm3));
    }
}
