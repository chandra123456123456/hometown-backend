package com.hometown.payment.service;

import com.hometown.common.web.ApiException;
import com.hometown.payment.domain.Payment;
import com.hometown.payment.domain.PaymentStatus;
import com.hometown.payment.dto.PaymentRequest;
import com.hometown.payment.dto.PaymentResponse;
import com.hometown.payment.gateway.PaymentGateway;
import com.hometown.payment.gateway.PaymentResult;
import com.hometown.payment.repo.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentGateway gateway;

    public PaymentService(PaymentRepository repository, PaymentGateway gateway) {
        this.repository = repository;
        this.gateway = gateway;
    }

    @Transactional
    public PaymentResponse process(PaymentRequest req) {
        PaymentResult result = gateway.charge(req.orderId(), req.amount());

        Payment payment = new Payment();
        payment.setOrderId(req.orderId());
        payment.setAmount(req.amount());
        payment.setStatus(result.success() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setMockTransactionId(result.transactionId());

        payment = repository.save(payment);
        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByOrder(Long orderId) {
        Payment payment = repository.findByOrderId(orderId)
                .orElseThrow(() -> ApiException.notFound("Payment not found for orderId: " + orderId));
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getOrderId(),
                p.getAmount(),
                p.getStatus(),
                p.getMockTransactionId(),
                p.getCreatedAt()
        );
    }
}
