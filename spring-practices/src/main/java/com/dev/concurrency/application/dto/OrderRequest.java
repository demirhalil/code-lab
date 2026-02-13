package com.dev.concurrency.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
    String customerId,
    List<OrderItemRequest> items
) {
    public record OrderItemRequest(
        String productCode,
        Integer quantity,
        BigDecimal price
    ) {}
}
