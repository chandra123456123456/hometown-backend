package com.hometown.cart.web;

import com.hometown.cart.dto.*;
import com.hometown.cart.service.CartService;
import com.hometown.common.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart(CurrentUser.id()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addOrUpdateItem(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addOrUpdateItem(CurrentUser.id(), request));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(CurrentUser.id(), productId));
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart() {
        return ResponseEntity.ok(cartService.clear(CurrentUser.id()));
    }

    @PostMapping("/flush")
    public ResponseEntity<CartResponse> flush(@Valid @RequestBody GuestCartRequest request) {
        return ResponseEntity.ok(cartService.flush(CurrentUser.id(), request));
    }
}
