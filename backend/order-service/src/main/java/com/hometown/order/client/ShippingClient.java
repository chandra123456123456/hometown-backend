package com.hometown.order.client;

import com.hometown.order.dto.ShippingEstimateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "SHIPPING-SERVICE")
public interface ShippingClient {

    @GetMapping("/api/shipping/estimate")
    ShippingEstimateDto getEstimate(@RequestParam("pincode") String pincode,
                                    @RequestParam("weight") double weight);
}
