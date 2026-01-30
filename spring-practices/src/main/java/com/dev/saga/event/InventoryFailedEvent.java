package com.dev.saga.event;

import java.util.UUID;

public record InventoryFailedEvent(UUID orderId, String reason) {}
