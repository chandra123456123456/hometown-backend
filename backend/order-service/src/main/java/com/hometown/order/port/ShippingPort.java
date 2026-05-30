package com.hometown.order.port;

import com.hometown.order.dto.ShippingEstimateDto;

public interface ShippingPort {
    ShippingEstimateDto estimate(String pincode, int weight);
}
