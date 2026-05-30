package com.hometown.shipping.web;

import com.hometown.shipping.dto.QuoteRequest;
import com.hometown.shipping.dto.ServiceabilityResponse;
import com.hometown.shipping.provider.ShippingOption;
import com.hometown.shipping.service.ShippingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping("/serviceability")
    public ResponseEntity<ServiceabilityResponse> serviceability(@RequestParam String pincode) {
        boolean serviceable = shippingService.isServiceable(pincode);
        return ResponseEntity.ok(new ServiceabilityResponse(pincode, serviceable));
    }

    @GetMapping("/estimate")
    public ResponseEntity<ShippingOption> estimate(
            @RequestParam String pincode,
            @RequestParam(defaultValue = "500") int weight) {
        ShippingOption option = shippingService.estimate(pincode, weight);
        return ResponseEntity.ok(option);
    }

    @PostMapping("/quotes")
    public ResponseEntity<List<ShippingOption>> quotes(@Valid @RequestBody QuoteRequest request) {
        List<ShippingOption> options = shippingService.quotes(request.pincode(), request.weightGrams());
        return ResponseEntity.ok(options);
    }
}
