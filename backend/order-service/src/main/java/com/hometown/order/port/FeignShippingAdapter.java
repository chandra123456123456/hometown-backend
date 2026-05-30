package com.hometown.order.port;

import com.hometown.order.client.ShippingClient;
import com.hometown.order.dto.ShippingEstimateDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("microservices")
public class FeignShippingAdapter implements ShippingPort {

    private final ShippingClient shippingClient;

    public FeignShippingAdapter(ShippingClient shippingClient) {
        this.shippingClient = shippingClient;
    }

    @Override
    public ShippingEstimateDto estimate(String pincode, int weight) {
        return shippingClient.getEstimate(pincode, weight);
    }
}
