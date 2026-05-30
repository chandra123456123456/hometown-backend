package com.hometown.cart.service;

import com.hometown.cart.domain.Cart;
import com.hometown.cart.domain.CartItem;
import com.hometown.cart.dto.*;
import com.hometown.cart.repo.CartItemRepository;
import com.hometown.cart.repo.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUserId(userId);
            return cartRepository.save(cart);
        });
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return toResponse(cart);
    }

    public CartResponse addOrUpdateItem(Long userId, CartItemRequest req) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.findByCartIdAndProductId(cart.getId(), req.productId())
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + req.quantity()),
                        () -> {
                            CartItem item = new CartItem();
                            item.setCartId(cart.getId());
                            item.setProductId(req.productId());
                            item.setQuantity(req.quantity());
                            cartItemRepository.save(item);
                        }
                );
        cartRepository.flush();
        return toResponse(cartRepository.findByUserId(userId).orElse(cart));
    }

    public CartResponse removeItem(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
        return toResponse(cartRepository.findByUserId(userId).orElse(cart));
    }

    public CartResponse clear(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteAllByCartId(cart.getId());
        return toResponse(cartRepository.findByUserId(userId).orElse(cart));
    }

    public CartResponse flush(Long userId, GuestCartRequest guestCartRequest) {
        Cart cart = getOrCreateCart(userId);
        for (CartItemRequest req : guestCartRequest.items()) {
            cartItemRepository.findByCartIdAndProductId(cart.getId(), req.productId())
                    .ifPresentOrElse(
                            existing -> existing.setQuantity(existing.getQuantity() + req.quantity()),
                            () -> {
                                CartItem item = new CartItem();
                                item.setCartId(cart.getId());
                                item.setProductId(req.productId());
                                item.setQuantity(req.quantity());
                                cartItemRepository.save(item);
                            }
                    );
        }
        cartRepository.flush();
        return toResponse(cartRepository.findByUserId(userId).orElse(cart));
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemDto> items = cart.getItems().stream()
                .map(i -> new CartItemDto(i.getProductId(), i.getQuantity()))
                .toList();
        return new CartResponse(cart.getUserId(), items);
    }
}
