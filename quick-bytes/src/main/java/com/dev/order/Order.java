package com.dev.order;

import java.math.BigDecimal;
import java.util.List;

public record Order(
        String id,
        String customerId,
        String restaurantId,
        List<String> items,
        BigDecimal totalAmount,
        String paymentId,
        String paymentConfirmation,
        OrderStatus status
) {
    // Convenience constructor for creating a new orders(before payment confirmation)
    public Order(final String id,
                 final String customerId,
                 final String restaurantId,
                 final List<String> items,
                 final BigDecimal totalAmount,
                 final String paymentId) {
        this(id, customerId, restaurantId, items, totalAmount, paymentId, null, OrderStatus.PENDING);
    }

    // Convenience method to create a copy with updated payment confirmation
    public Order withPaymentConfirmation(final String paymentConfirmation) {
        return new Order(id, customerId, restaurantId, items, totalAmount, paymentId, paymentConfirmation, status);
    }

    // Convenience method to create a copy with updated status
    public Order withStatus(final OrderStatus status) {
        return new Order(id, customerId, restaurantId, items, totalAmount, paymentId, paymentConfirmation, status);
    }

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PREPARING,
        OUT_FOR_DELIVERY,
        DELIVERED,
        CANCELLED
    }
}
