package com.dev.concurrency;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ReservationInventoryService {

    private final InventoryRepository repository;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicInteger inMemoryStock = new AtomicInteger(100);

    public ReservationInventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    /**
     * UNPROTECTED: Demonstrates a Race Condition.
     * Multiple threads can read the same stock value before any of them updates it.
     */
    @Transactional
    public void reserveUnsafe(String productCode) {
        Inventory inventory = repository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (inventory.getStock() > 0) {
            // Simulate processing time to increase race condition probability
            simulateDelay(10);
            inventory.setStock(inventory.getStock() - 1);
            repository.saveAndFlush(inventory);
        }
    }

    /**
     * SYNCHRONIZED: Thread-safe but serializes all requests.
     * Good for simple cases, but can become a bottleneck.
     */
    @Transactional
    public synchronized void reserveSynchronized(String productCode) {
        reserveUnsafe(productCode);
    }

    /**
     * REENTRANT LOCK: More flexible than synchronized.
     * Allows for tryLock() and better control.
     */
    @Transactional
    public void reserveWithLock(String productCode) {
        lock.lock();
        try {
            reserveUnsafe(productCode);
        } finally {
            lock.unlock();
        }
    }

    /**
     * ATOMIC VARIABLES: Extremely efficient for simple counter updates.
     * Uses CAS (Compare-And-Swap) at the CPU level.
     */
    public int reserveAtomic() {
        return inMemoryStock.decrementAndGet();
    }

    /**
     * OPTIMISTIC LOCKING: Uses JPA @Version.
     * Most scalable for databases. Throws ObjectOptimisticLockingFailureException on conflict.
     */
    @Transactional
    public void reserveOptimistic(String productCode) {
        // JPA handles the version check automatically upon commit/flush
        Inventory inventory = repository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (inventory.getStock() > 0) {
            inventory.setStock(inventory.getStock() - 1);
            repository.saveAndFlush(inventory);
        }
    }

    /**
     * ASYNC: Demonstrates background processing using CompletableFuture.
     */
    @Async("taskExecutor")
    public CompletableFuture<String> processReservationAsync(String productCode) {
        simulateDelay(100);
        return CompletableFuture.completedFuture("Reservation processed for: " + productCode);
    }

    private void simulateDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
