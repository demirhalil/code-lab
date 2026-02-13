package com.dev.concurrency;

import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final ReservationInventoryService inventoryService;

    public InventoryController(ReservationInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reserve/unsafe/{code}")
    public void reserveUnsafe(@PathVariable String code) {
        inventoryService.reserveUnsafe(code);
    }

    @PostMapping("/reserve/sync/{code}")
    public void reserveSync(@PathVariable String code) {
        inventoryService.reserveSynchronized(code);
    }

    @PostMapping("/reserve/lock/{code}")
    public void reserveLock(@PathVariable String code) {
        inventoryService.reserveWithLock(code);
    }

    @PostMapping("/reserve/optimistic/{code}")
    public void reserveOptimistic(@PathVariable String code) {
        inventoryService.reserveOptimistic(code);
    }

    @PostMapping("/reserve/atomic")
    public int reserveAtomic() {
        return inventoryService.reserveAtomic();
    }

    @PostMapping("/reserve/async/{code}")
    public CompletableFuture<String> reserveAsync(@PathVariable String code) {
        return inventoryService.processReservationAsync(code);
    }
}
