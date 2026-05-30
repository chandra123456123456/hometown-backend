package com.hometown.shipping.provider;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MockShippingProvider implements ShippingProvider {

    @Override
    public String name() {
        return "MOCK";
    }

    @Override
    public boolean isServiceable(String destPincode) {
        return destPincode != null && destPincode.matches("\\d{6}");
    }

    @Override
    public int estimateEtaDays(String destPincode, Parcel parcel) {
        int firstDigit = Character.getNumericValue(destPincode.charAt(0));
        return 2 + (firstDigit % 5);
    }

    @Override
    public BigDecimal calculateCharge(String destPincode, Parcel parcel) {
        long slabs = (long) Math.ceil(parcel.weightGrams() / 500.0);
        return BigDecimal.valueOf(50 + slabs * 20);
    }
}
