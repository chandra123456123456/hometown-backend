package com.hometown.shipping.provider;

import java.math.BigDecimal;

public record ShippingOption(String partner, boolean serviceable, int etaDays, BigDecimal charge) {}
