package com.dev.saga.service;

import com.dev.saga.event.InventoryAllocatedEvent;
import com.dev.saga.event.InventoryFailedEvent;
import com.dev.saga.event.OrderCreatedEvent;
import com.dev.saga.event.PaymentFailedEvent;
import com.dev.saga.event.PaymentProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final Map<String, AtomicInteger> stock = new ConcurrentHashMap<>();
    private final Map<UUID, OrderDetails> pendingOrders = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public InventoryService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        // Seed some stock
        stock.put("Laptop", new AtomicInteger(10));
        stock.put("Phone", new AtomicInteger(0)); // Out of stock to simulate failure
    }

    private record OrderDetails(String product, int quantity) {}

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Inventory received order details for: {}", event.orderId());
        pendingOrders.put(event.orderId(), new OrderDetails(event.product(), event.quantity()));
    }

    @EventListener
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        UUID orderId = event.orderId();
        OrderDetails details = pendingOrders.get(orderId);
        
        if (details == null) {
            log.error("No details found for order: {}", orderId);
            return;
        }

        log.info("Allocating {} units of {} for order: {}", details.quantity(), details.product(), orderId);
        
        AtomicInteger availableStock = stock.get(details.product());
        if (availableStock != null && availableStock.get() >= details.quantity()) {
            availableStock.addAndGet(-details.quantity());
            log.info("Inventory allocated successfully for order: {}", orderId);
            eventPublisher.publishEvent(new InventoryAllocatedEvent(orderId));
        } else {
            log.error("Inventory allocation failed for order: {}. Not enough stock for {}", orderId, details.product());
            eventPublisher.publishEvent(new InventoryFailedEvent(orderId, "Out of stock"));
        }
    }

    @EventListener
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.info("Payment failed for order: {}. Cleaning up inventory pending data.", event.orderId());
        pendingOrders.remove(event.orderId());
    }
}
