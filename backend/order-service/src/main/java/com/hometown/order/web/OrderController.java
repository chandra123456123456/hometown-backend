package com.hometown.order.web;

import com.hometown.common.security.CurrentUser;
import com.hometown.order.dto.CreateOrderRequest;
import com.hometown.order.dto.OrderResponse;
import com.hometown.order.dto.UpdateOrderStatusRequest;
import com.hometown.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(CurrentUser.id(), req));
    }

    @GetMapping
    public List<OrderResponse> list() {
        return orderService.list(CurrentUser.id());
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return orderService.get(CurrentUser.id(), id);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> adminListAll() {
        return orderService.listAll();
    }

    @PutMapping("/admin/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse adminUpdateStatus(@Valid @RequestBody UpdateOrderStatusRequest req) {
        return orderService.updateStatus(req.orderId(), req.status());
    }
}
