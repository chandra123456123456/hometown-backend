package com.hometown.order.service;

import com.hometown.common.web.ApiException;
import com.hometown.order.domain.Order;
import com.hometown.order.domain.OrderItem;
import com.hometown.order.domain.OrderStatus;
import com.hometown.order.dto.*;
import com.hometown.order.port.PricingPort;
import com.hometown.order.port.ShippingPort;
import com.hometown.order.repo.OrderItemRepository;
import com.hometown.order.repo.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PricingPort pricingPort;
    private final ShippingPort shippingPort;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        PricingPort pricingPort,
                        ShippingPort shippingPort) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.pricingPort = pricingPort;
        this.shippingPort = shippingPort;
    }

    public StockCheckResponse validateStock(List<OrderLineRequest> items) {
        List<StockIssue> issues = new ArrayList<>();
        for (OrderLineRequest line : items) {
            try {
                ProductDto product = pricingPort.fetchProduct(line.productId());
                int available = product.stock() == null ? 0 : product.stock();
                if (line.quantity() > available) {
                    issues.add(new StockIssue(line.productId(), line.quantity(), available));
                }
            } catch (Exception ignored) {
                // skip item on fetch failure
            }
        }
        return new StockCheckResponse(issues.isEmpty(), issues);
    }

    @Transactional
    public OrderResponse create(Long userId, CreateOrderRequest req) {
        StockCheckResponse stockCheck = validateStock(req.items());
        if (!stockCheck.ok()) {
            throw ApiException.conflict("Insufficient stock for one or more items");
        }

        List<OrderItem> items = req.items().stream().map(line -> {
            BigDecimal price = BigDecimal.ZERO;
            Long sellerId = null;
            try {
                ProductDto product = pricingPort.fetchProduct(line.productId());
                price = product.price();
                sellerId = product.sellerId();
            } catch (Exception ignored) {
                // fall back to zero price
            }
            OrderItem item = new OrderItem();
            item.setProductId(line.productId());
            item.setQuantity(line.quantity());
            item.setPrice(price);
            item.setSellerId(sellerId);
            return item;
        }).toList();

        BigDecimal subtotal = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingCost = BigDecimal.ZERO;
        String partner = req.shippingPartner();
        int etaDays = 5;
        try {
            ShippingEstimateDto estimate = shippingPort.estimate(req.destPincode(), 1);
            shippingCost = estimate.charge();
            partner = estimate.partner();
            etaDays = estimate.etaDays();
        } catch (Exception ignored) {
            // fall back to defaults
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(subtotal.add(shippingCost));
        order.setShippingAddress(req.shippingAddress());
        order.setDestPincode(req.destPincode());
        order.setShippingPartner(partner);
        order.setShippingCost(shippingCost);
        order.setEstimatedDeliveryDays(etaDays);
        order = orderRepository.save(order);

        final Long orderId = order.getId();
        items.forEach(i -> i.setOrderId(orderId));
        List<OrderItem> savedItems = orderItemRepository.saveAll(items);

        return toResponse(order, savedItems);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listAll() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(order -> toResponse(order, orderItemRepository.findByOrderId(order.getId())))
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("Invalid status: " + status);
        }
        order.setStatus(newStatus);
        order = orderRepository.save(order);
        return toResponse(order, orderItemRepository.findByOrderId(orderId));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(order -> toResponse(order, orderItemRepository.findByOrderId(order.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse get(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));
        if (!order.getUserId().equals(userId)) {
            throw ApiException.notFound("Order not found");
        }
        return toResponse(order, orderItemRepository.findByOrderId(orderId));
    }

    private OrderResponse toResponse(Order order, List<OrderItem> items) {
        List<OrderItemDto> itemDtos = items.stream()
                .map(i -> new OrderItemDto(i.getId(), i.getProductId(), i.getQuantity(), i.getPrice(), i.getSellerId()))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getDestPincode(),
                order.getShippingPartner(),
                order.getShippingCost(),
                order.getEstimatedDeliveryDays(),
                order.getCreatedAt(),
                itemDtos
        );
    }
}
