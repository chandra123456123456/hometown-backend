package com.hometown.payment.gateway;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult charge(Long orderId, BigDecimal amount) {
        return new PaymentResult(true, "MOCK-" + UUID.randomUUID());
    }
}
