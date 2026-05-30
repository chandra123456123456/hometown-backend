package com.hometown.order.custom.web;

import com.hometown.common.security.CurrentUser;
import com.hometown.order.custom.dto.CustomOrderAdminUpdate;
import com.hometown.order.custom.dto.CustomOrderRequest;
import com.hometown.order.custom.dto.CustomOrderResponse;
import com.hometown.order.custom.service.CustomOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/custom-orders")
public class CustomOrderController {

    private final CustomOrderService service;

    public CustomOrderController(CustomOrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CustomOrderResponse> raise(@Valid @RequestBody CustomOrderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.raise(CurrentUser.id(), req));
    }

    @GetMapping
    public List<CustomOrderResponse> listMine() {
        return service.listMine(CurrentUser.id());
    }

    @GetMapping("/admin/all")
    public List<CustomOrderResponse> listAll() {
        return service.listAll();
    }

    @PutMapping("/admin/update")
    public CustomOrderResponse adminUpdate(@Valid @RequestBody CustomOrderAdminUpdate update) {
        return service.adminUpdate(update);
    }
}
