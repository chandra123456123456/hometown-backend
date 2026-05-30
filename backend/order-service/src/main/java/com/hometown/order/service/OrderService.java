package com.hometown.order.service;

import com.hometown.common.web.ApiException;
import com.hometown.order.client.ProductClient;
import com.hometown.order.client.ShippingClient;
import com.hometown.order.domain.Order;
import com.hometown.order.domain.OrderItem;
import com.hometown.order.domain.OrderStatus;
import com.hometown.order.dto.*;
import com.hometown.order.repo.OrderItemRepository;
import com.hometown.order.repo.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final ShippingClient shippingClient;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductClient productClient,
                        ShippingClient shippingClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productClient = productClient;
        this.shippingClient = shippingClient;
    }

    @Transactional
    public OrderResponse create(Long userId, CreateOrderRequest req) {
        List<OrderItem> items = req.items().stream().map(line -> {
            BigDecimal price = BigDecimal.ZERO;
            Long sellerId = null;
            try {
                ProductDto product = productClient.getProduct(line.productId());
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
            ShippingEstimateDto estimate = shippingClient.getEstimate(req.destPincode(), 1.0);
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
