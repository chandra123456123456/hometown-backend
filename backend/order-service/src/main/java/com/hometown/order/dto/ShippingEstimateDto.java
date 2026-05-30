package com.hometown.order.dto;

import java.math.BigDecimal;

public record ShippingEstimateDto(String partner, boolean serviceable, int etaDays, BigDecimal charge) {
}
