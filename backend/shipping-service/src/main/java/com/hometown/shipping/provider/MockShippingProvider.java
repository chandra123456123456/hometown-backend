package com.hometown.shipping.provider;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class MockShippingProvider implements ShippingProvider {

    // Zone bands by first PIN digit (Indian courier approximation):
    //   1,2 -> NORTH-LOCAL  base=40  perKg=30  eta=2
    //   3,4 -> WEST         base=50  perKg=35  eta=3
    //   5,6 -> SOUTH        base=60  perKg=40  eta=4
    //   else -> EAST/NE     base=90  perKg=60  eta=6
    private record Zone(int base, int perKg, int etaDays) {}

    private Zone zone(String destPincode) {
        int d = Character.getNumericValue(destPincode.charAt(0));
        if (d == 1 || d == 2) return new Zone(40, 30, 2);
        if (d == 3 || d == 4) return new Zone(50, 35, 3);
        if (d == 5 || d == 6) return new Zone(60, 40, 4);
        return new Zone(90, 60, 6);
    }

    private int chargeableKg(Parcel parcel) {
        double actual = parcel.weightGrams() / 1000.0;
        double volumetric = parcel.volumeCm3() / 5000.0;
        return Math.max(1, (int) Math.ceil(Math.max(actual, volumetric)));
    }

    @Override
    public String name() { return "MOCK"; }

    @Override
    public boolean isServiceable(String destPincode) {
        return destPincode != null && destPincode.matches("\\d{6}");
    }

    @Override
    public int estimateEtaDays(String destPincode, Parcel parcel) {
        return zone(destPincode).etaDays();
    }

    @Override
    public BigDecimal calculateCharge(String destPincode, Parcel parcel) {
        Zone z = zone(destPincode);
        int kg = chargeableKg(parcel);
        return BigDecimal.valueOf((long) z.base() + (long) z.perKg() * kg).setScale(2, RoundingMode.UNNECESSARY);
    }
}
