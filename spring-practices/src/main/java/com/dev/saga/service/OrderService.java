package com.dev.saga.service;

import com.dev.saga.event.InventoryAllocatedEvent;
import com.dev.saga.event.InventoryFailedEvent;
import com.dev.saga.event.OrderCreatedEvent;
import com.dev.saga.event.PaymentFailedEvent;
import com.dev.saga.event.PaymentProcessedEvent;
import com.dev.saga.model.Order;
import com.dev.saga.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public Order createOrder(String product, int quantity, BigDecimal price) {
        Order order = new Order(UUID.randomUUID(), product, quantity, price, OrderStatus.PENDING);
        orders.put(order.id(), order);
        log.info("Order created: {}", order.id());
        eventPublisher.publishEvent(new OrderCreatedEvent(order.id(), price.multiply(BigDecimal.valueOf(quantity)), product, quantity));
        return order;
    }

    public Order getOrder(UUID id) {
        return orders.get(id);
    }

    public Collection<Order> getAllOrders() {
        return orders.values();
    }

    @EventListener
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        log.info("Payment processed for order: {}", event.orderId());
        orders.computeIfPresent(event.orderId(), (id, order) -> {
            if (order.status() == OrderStatus.PENDING) {
                return order.withStatus(OrderStatus.PAID);
            }
            return order;
        });
    }

    @EventListener
    public void onPaymentFailed(PaymentFailedEvent event) {
        log.error("Payment failed for order: {}. Reason: {}", event.orderId(), event.reason());
        updateOrderStatus(event.orderId(), OrderStatus.PAYMENT_FAILED);
        cancelOrder(event.orderId());
    }

    @EventListener
    public void onInventoryAllocated(InventoryAllocatedEvent event) {
        log.info("Inventory allocated for order: {}", event.orderId());
        updateOrderStatus(event.orderId(), OrderStatus.COMPLETED);
    }

    @EventListener
    public void onInventoryFailed(InventoryFailedEvent event) {
        log.error("Inventory allocation failed for order: {}. Reason: {}", event.orderId(), event.reason());
        updateOrderStatus(event.orderId(), OrderStatus.INVENTORY_FAILED);
        // Compensation logic: In a real scenario, we might need to refund payment
        // Here, we just mark it as failed/cancelled
        cancelOrder(event.orderId());
    }

    private void updateOrderStatus(UUID orderId, OrderStatus status) {
        orders.computeIfPresent(orderId, (id, order) -> order.withStatus(status));
    }

    private void cancelOrder(UUID orderId) {
        log.warn("Cancelling order: {}", orderId);
        updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }
}
