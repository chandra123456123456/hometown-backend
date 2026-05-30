package com.hometown.order.dto;

public record StockIssue(Long productId, int requested, int available) {
}
