package com.dev.saga.event;

import java.util.UUID;

public record PaymentFailedEvent(UUID orderId, String reason) {}
