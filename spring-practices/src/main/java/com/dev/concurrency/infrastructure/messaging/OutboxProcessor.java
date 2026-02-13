package com.dev.concurrency.infrastructure.messaging;

import com.dev.concurrency.application.service.InventorySagaService;
import com.dev.concurrency.domain.model.Order;
import com.dev.concurrency.domain.model.OutboxEvent;
import com.dev.concurrency.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final InventorySagaService inventorySagaService;
    private final ObjectMapper objectMapper;

    /**
     * Outbox Processor:
     * - Periodically scans the outbox table for unprocessed events.
     * - Ensures "At Least Once" delivery (can be "Exactly Once" if consumer is idempotent).
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> events = outboxRepository.findByProcessedFalse();
        
        for (OutboxEvent event : events) {
            try {
                log.info("Processing outbox event: {} of type {}", event.getId(), event.getEventType());
                
                if ("ORDER_CREATED".equals(event.getEventType())) {
                    Order order = objectMapper.readValue(event.getPayload(), Order.class);
                    // Start the Saga Step 2
                    inventorySagaService.processInventory(order);
                }
                
                event.setProcessed(true);
                outboxRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to process outbox event: {}", event.getId(), e);
            }
        }
    }
}
