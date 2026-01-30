package com.dev.saga.event;

import java.util.UUID;

public record PaymentProcessedEvent(UUID orderId) {}
