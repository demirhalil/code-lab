package com.dev.concurrency.application.service;

import com.dev.concurrency.application.dto.OrderRequest;
import com.dev.concurrency.domain.model.Order;
import com.dev.concurrency.domain.model.OutboxEvent;
import com.dev.concurrency.domain.repository.OrderRepository;
import com.dev.concurrency.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Outbox Pattern Implementation:
     * - Order and OutboxEvent are saved within the SAME transaction.
     * - Ensures atomicity: if order fails, no event is saved. If event save fails, order rolls back.
     */
    @Transactional
    public UUID placeOrder(OrderRequest request) {
        String correlationId = MDC.get("correlationId");
        log.info("Processing order for customer: {} with correlationId: {}", request.customerId(), correlationId);

        Order order = new Order(request.customerId());
        request.items().forEach(item -> 
            order.addItem(item.productCode(), item.quantity(), item.price())
        );

        Order savedOrder = orderRepository.save(order);

        // Save to Outbox
        createOutboxEvent(savedOrder, "ORDER_CREATED");

        return savedOrder.getId();
    }

    private void createOutboxEvent(Order order, String eventType) {
        try {
            String payload = objectMapper.writeValueAsString(order);
            OutboxEvent event = new OutboxEvent("Order", order.getId(), eventType, payload);
            outboxRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize order for outbox", e);
        }
    }
}
