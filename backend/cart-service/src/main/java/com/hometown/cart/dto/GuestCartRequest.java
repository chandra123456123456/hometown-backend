package com.hometown.cart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GuestCartRequest(@NotNull @Valid List<CartItemRequest> items) {}
