package com.hometown.order.custom.service;

import com.hometown.common.web.ApiException;
import com.hometown.order.custom.domain.CustomOrder;
import com.hometown.order.custom.dto.CustomOrderAdminUpdate;
import com.hometown.order.custom.dto.CustomOrderRequest;
import com.hometown.order.custom.dto.CustomOrderResponse;
import com.hometown.order.custom.repo.CustomOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class CustomOrderService {

    private static final Set<String> VALID_TYPES = Set.of("PAINTING", "SKETCH");

    private final CustomOrderRepository repo;

    public CustomOrderService(CustomOrderRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public CustomOrderResponse raise(Long userId, CustomOrderRequest req) {
        String type = req.type().toUpperCase();
        if (!VALID_TYPES.contains(type)) {
            throw ApiException.badRequest("type must be PAINTING or SKETCH");
        }
        CustomOrder order = new CustomOrder();
        order.setUserId(userId);
        order.setCustomerName(req.customerName());
        order.setCustomerPhone(req.customerPhone());
        order.setType(type);
        order.setDescription(req.description());
        order.setStatus("REQUESTED");
        return toResponse(repo.save(order));
    }

    @Transactional(readOnly = true)
    public List<CustomOrderResponse> listMine(Long userId) {
        return repo.findByUserIdOrderByIdDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomOrderResponse> listAll() {
        return repo.findAllByOrderByIdDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CustomOrderResponse adminUpdate(CustomOrderAdminUpdate u) {
        CustomOrder order = repo.findById(u.id())
                .orElseThrow(() -> ApiException.notFound("Custom order not found"));
        if (u.description() != null) order.setDescription(u.description());
        if (u.status() != null) order.setStatus(u.status());
        if (u.quotedPrice() != null) order.setQuotedPrice(u.quotedPrice());
        if (u.deliveryDate() != null) order.setDeliveryDate(u.deliveryDate());
        if (u.adminNotes() != null) order.setAdminNotes(u.adminNotes());
        return toResponse(repo.save(order));
    }

    private CustomOrderResponse toResponse(CustomOrder o) {
        return new CustomOrderResponse(
                o.getId(),
                o.getUserId(),
                o.getCustomerName(),
                o.getCustomerPhone(),
                o.getType(),
                o.getDescription(),
                o.getStatus(),
                o.getQuotedPrice(),
                o.getDeliveryDate(),
                o.getAdminNotes(),
                o.getCreatedAt(),
                o.getUpdatedAt()
        );
    }
}
