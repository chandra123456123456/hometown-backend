package com.hometown.order.port;

import com.hometown.order.dto.ShippingEstimateDto;

import java.util.List;

public interface ShippingPort {
    ShippingEstimateDto estimate(String pincode, int weightGrams, int volumeCm3);

    List<ShippingEstimateDto> quotes(String pincode, int weightGrams, int volumeCm3);
}
