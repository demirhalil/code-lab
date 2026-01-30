package com.dev.saga.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(UUID orderId, BigDecimal amount, String product, int quantity) {}
