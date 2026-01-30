package com.dev.saga.model;

public enum OrderStatus {
    PENDING,
    PAID,
    INVENTORY_ALLOCATED,
    COMPLETED,
    PAYMENT_FAILED,
    INVENTORY_FAILED,
    CANCELLED
}
