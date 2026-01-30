package com.dev.saga.service;

import com.dev.saga.event.InventoryFailedEvent;
import com.dev.saga.event.OrderCreatedEvent;
import com.dev.saga.event.PaymentFailedEvent;
import com.dev.saga.event.PaymentProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final Map<UUID, BigDecimal> processedPayments = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public PaymentService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Processing payment for order: {}. Amount: {}", event.orderId(), event.amount());

        // Simulate payment logic
        if (event.amount().compareTo(new BigDecimal("1000")) > 0) {
            log.error("Payment failed for order: {}. Insufficient funds.", event.orderId());
            eventPublisher.publishEvent(new PaymentFailedEvent(event.orderId(), "Insufficient funds"));
        } else {
            processedPayments.put(event.orderId(), event.amount());
            log.info("Payment successful for order: {}", event.orderId());
            eventPublisher.publishEvent(new PaymentProcessedEvent(event.orderId()));
        }
    }

    @EventListener
    public void onInventoryFailed(InventoryFailedEvent event) {
        log.warn("Inventory failed for order: {}. Reversing payment (Compensation).", event.orderId());
        refundPayment(event.orderId());
    }

    private void refundPayment(UUID orderId) {
        BigDecimal amount = processedPayments.remove(orderId);
        if (amount != null) {
            log.info("Refunded {} for order: {}", amount, orderId);
        } else {
            log.warn("No payment found to refund for order: {}", orderId);
        }
    }
}
