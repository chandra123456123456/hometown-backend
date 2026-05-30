package com.hometown.order.client;

import com.hometown.order.dto.ShippingEstimateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "SHIPPING-SERVICE")
public interface ShippingClient {

    @GetMapping("/api/shipping/estimate")
    ShippingEstimateDto getEstimate(@RequestParam("pincode") String pincode,
                                    @RequestParam("weight") int weight,
                                    @RequestParam("volume") int volume);

    @PostMapping("/api/shipping/quotes")
    List<ShippingEstimateDto> getQuotes(@RequestBody QuoteBody body);

    record QuoteBody(String pincode, int weightGrams, int volumeCm3) {
    }
}
