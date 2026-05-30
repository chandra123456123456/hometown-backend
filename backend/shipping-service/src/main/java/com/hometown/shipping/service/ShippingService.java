package com.hometown.shipping.service;

import com.hometown.shipping.provider.Parcel;
import com.hometown.shipping.provider.ShippingOption;
import com.hometown.shipping.provider.ShippingProvider;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ShippingService {

    private final List<ShippingProvider> providers;

    public ShippingService(List<ShippingProvider> providers) {
        this.providers = providers;
    }

    public boolean isServiceable(String pincode) {
        return providers.stream().anyMatch(p -> p.isServiceable(pincode));
    }

    public ShippingOption estimate(String pincode, Parcel parcel) {
        List<ShippingOption> options = buildOptions(pincode, parcel);
        return options.stream()
                .filter(ShippingOption::serviceable)
                .min(Comparator.comparing(ShippingOption::charge))
                .orElseGet(() -> options.isEmpty() ? null : options.get(0));
    }

    public List<ShippingOption> quotes(String pincode, Parcel parcel) {
        return buildOptions(pincode, parcel);
    }

    private List<ShippingOption> buildOptions(String pincode, Parcel parcel) {
        return providers.stream().map(p -> {
            boolean serviceable = p.isServiceable(pincode);
            int etaDays = serviceable ? p.estimateEtaDays(pincode, parcel) : 0;
            var charge = serviceable ? p.calculateCharge(pincode, parcel) : java.math.BigDecimal.ZERO;
            return new ShippingOption(p.name(), serviceable, etaDays, charge);
        }).toList();
    }
}
