package com.hometown.payment.gateway;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult charge(Long orderId, BigDecimal amount);
}
