package com.dev.concurrency.application.service;

import com.dev.concurrency.domain.model.Order;
import com.dev.concurrency.domain.model.Stock;
import com.dev.concurrency.domain.repository.OrderRepository;
import com.dev.concurrency.domain.repository.StockRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventorySagaService {

    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;

    /**
     * Saga Choreography - Step 2: Inventory Reservation
     * Using Propagation.REQUIRES_NEW to ensure stock reservation is attempted 
     * in a separate transaction from the original order creation, 
     * but also making it @Async to not block the main request thread.
     */
    @Async("orderTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackInventory")
    public void processInventory(Order order) {
        log.info("Processing inventory for order: {}", order.getId());

        try {
            order.getItems().forEach(item -> {
                Stock stock = stockRepository.findById(item.getProductCode())
                        .orElseThrow(() -> new RuntimeException("Stock not found for " + item.getProductCode()));
                
                // State Protection: Optimistic Locking handled by JPA @Version in Stock entity
                stock.reserve(item.getQuantity());
                stockRepository.save(stock);
            });

            order.markAsValidated();
            order.markAsCompleted();
            orderRepository.save(order);
            log.info("Inventory reserved and order completed: {}", order.getId());
            
            // In a real system, we would trigger the next step (Payment) here via Outbox/Messaging
        } catch (Exception e) {
            log.error("Failed to reserve inventory for order: {}. Reason: {}", order.getId(), e.getMessage());
            compensateInventory(order, e.getMessage());
        }
    }

    public void fallbackInventory(Order order, Throwable t) {
        log.error("Inventory Service is unavailable or slow. Circuit Breaker triggered for order: {}", order.getId());
        compensateInventory(order, "Inventory Service Unavailable");
    }

    /**
     * Compensating Transaction: Rollback the order if inventory fails.
     */
    @Transactional
    public void compensateInventory(Order order, String reason) {
        Order persistentOrder = orderRepository.findById(order.getId()).orElse(order);
        persistentOrder.markAsFailed(reason);
        orderRepository.save(persistentOrder);
        log.info("Compensating transaction: Order {} marked as FAILED", order.getId());
    }
}
