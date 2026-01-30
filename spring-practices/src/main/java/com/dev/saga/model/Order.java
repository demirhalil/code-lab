package com.dev.saga.model;

import java.math.BigDecimal;
import java.util.UUID;

public record Order(
    UUID id,
    String product,
    int quantity,
    BigDecimal price,
    OrderStatus status
) {
    public Order withStatus(OrderStatus newStatus) {
        return new Order(id, product, quantity, price, newStatus);
    }
}
