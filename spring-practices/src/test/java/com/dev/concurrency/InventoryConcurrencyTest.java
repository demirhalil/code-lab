//package com.dev.concurrency;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//public class InventoryConcurrencyTest {
//
//    @Autowired
//    private ReservationInventoryService inventoryService;
//
//    @Autowired
//    private InventoryRepository inventoryRepository;
//
//    private static final String PRODUCT_CODE = "LAPTOP-001";
//    private static final int INITIAL_STOCK = 100;
//    private static final int THREAD_COUNT = 50;
//
//    @BeforeEach
//    public void setup() {
//        inventoryRepository.deleteAll();
//        inventoryRepository.save(new Inventory(PRODUCT_CODE, INITIAL_STOCK));
//    }
//
//    @Test
//    public void testUnsafeReservation_ShouldHaveRaceCondition() throws InterruptedException {
//        runConcurrentTest(() -> inventoryService.reserveUnsafe(PRODUCT_CODE));
//
//        Inventory inventory = inventoryRepository.findByProductCode(PRODUCT_CODE).get();
//        // In a perfect world, stock should be 50.
//        // Due to race condition, it will likely be higher than 50.
//        System.out.println("[DEBUG_LOG] Unsafe Stock: " + inventory.getStock());
//        assertTrue(inventory.getStock() > (INITIAL_STOCK - THREAD_COUNT),
//            "Expected race condition to occur (stock > 50)");
//    }
//
//    @Test
//    public void testSynchronizedReservation_ShouldBeThreadSafe() throws InterruptedException {
//        runConcurrentTest(() -> inventoryService.reserveSynchronized(PRODUCT_CODE));
//
//        Inventory inventory = inventoryRepository.findByProductCode(PRODUCT_CODE).get();
//        System.out.println("[DEBUG_LOG] Synchronized Stock: " + inventory.getStock());
//        assertEquals(INITIAL_STOCK - THREAD_COUNT, inventory.getStock(), "Stock should be exactly 50");
//    }
//
//    @Test
//    public void testLockReservation_ShouldBeThreadSafe() throws InterruptedException {
//        runConcurrentTest(() -> inventoryService.reserveWithLock(PRODUCT_CODE));
//
//        Inventory inventory = inventoryRepository.findByProductCode(PRODUCT_CODE).get();
//        System.out.println("[DEBUG_LOG] Locked Stock: " + inventory.getStock());
//        assertEquals(INITIAL_STOCK - THREAD_COUNT, inventory.getStock(), "Stock should be exactly 50");
//    }
//
//    private void runConcurrentTest(Runnable task) throws InterruptedException {
//        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
//        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
//
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            executor.submit(() -> {
//                try {
//                    task.run();
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//        executor.shutdown();
//    }
//}
