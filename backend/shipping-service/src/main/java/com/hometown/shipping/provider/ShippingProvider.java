package com.hometown.shipping.provider;

import java.math.BigDecimal;

public interface ShippingProvider {

    String name();

    boolean isServiceable(String destPincode);

    int estimateEtaDays(String destPincode, Parcel parcel);

    BigDecimal calculateCharge(String destPincode, Parcel parcel);
}
