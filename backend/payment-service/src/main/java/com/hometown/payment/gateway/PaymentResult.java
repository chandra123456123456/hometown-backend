package com.hometown.payment.gateway;

public record PaymentResult(boolean success, String transactionId) {
}
