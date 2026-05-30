package com.hometown.order.dto;

import java.util.List;

public record StockCheckResponse(boolean ok, List<StockIssue> issues) {
}
