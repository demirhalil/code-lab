package com.dev.quickbytes.restaurant;

import com.dev.quickbytes.order.Order;
import com.dev.quickbytes.restaurant.Restaurant;
import com.dev.quickbytes.restaurant.RestaurantNotificationService;
import com.dev.quickbytes.restaurant.RestaurantService;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RestaurantController.class);
    private final RestaurantService restaurantService;
    private final RestaurantNotificationService notificationService;

    public RestaurantController(final RestaurantService restaurantService, final RestaurantNotificationService notificationService) {
        this.restaurantService = restaurantService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Restaurant> findAll() {
        return restaurantService.findAll();
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<Map<String, Object>> getMenu(@PathVariable String restaurantId) {
        LOGGER.info("API Request: Getting Menu for restaurant: {}", restaurantId);

        try {
            List<MenuItem> menuItems = restaurantService.getMenuFromPartner(restaurantId);
            return ResponseEntity.ok(
                    Map.of(
                            "restaurantId", restaurantId,
                            "menuItems", menuItems,
                            "count", menuItems.size(),
                            "message", "Menu retrieved successfully"
                    )
            );
        } catch (Exception e) {
            LOGGER.error("Failed to fetch menu after all retries:  {}", e.getMessage());
            return ResponseEntity.status(503).body(Map.of(
                    "message", e.getMessage(),
                    "error", "Service temporarily unavailable",
                    "restaurantId", restaurantId)
            );
        }
    }

    @GetMapping("/lunch-rush")
    public ResponseEntity<Map<String, Object>> getLunchRush() {
        LOGGER.info("LUNCH RUSH STARTED - Simulating 10 concurrent order notifications");
        LOGGER.info("Concurrency limit: 3 (only 3 notifications can process simultaneously");

        LocalDateTime startTime = LocalDateTime.now();

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            Order order = new Order(
                    String.format("lunch-%04d", i),
                    "customer-" + i,
                    "restaurant-001",
                    List.of("burger", "fries", "drink"),
                    new BigDecimal("15.99"),
                    "payment-" + i,
                    "confirmed-" + i,
                    Order.OrderStatus.CONFIRMED);
            orders.add(order);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        LOGGER.info("Submitting all 10 orders to thread pool...");

        for (Order order : orders) {
            executorService.submit(() -> {
                try {
                    this.notificationService.notifyRestaurant(order);
                } catch (Exception e) {
                    LOGGER.error("Error notifying restaurant for order {}: {}", order.id(), e.getMessage());
                }
            });
        }
        executorService.shutdown();

        try{
            boolean finished = executorService.awaitTermination(2, TimeUnit.MINUTES);
            if (!finished) {
                LOGGER.warn(("Some notifications did not complete withing 2 minutes"));
                executorService.shutdown();
            }
        }catch (InterruptedException e){
            LOGGER.error("Thread pool interrupted: {}", e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        LocalDateTime endTime = LocalDateTime.now();
        long duration = Duration.between(startTime, endTime).toSeconds();

        LOGGER.info("LUNCH RUSH COMPLETED - All 10 notification process in {} seconds", duration);
        LOGGER.info("Expected time: ~6-8 seconds (10 orders / 3 concurrent * 2s each)");

        return ResponseEntity.ok(Map.of(
                "message", "Lunch Rush simulation Completed",
                "totalOrders", 10,
                "concurrencyLimit", 3,
                "durationSeconds", duration,
                "expectedDuration", "6-8 seconds",
                "threadPoolType", "FixedThreadPool (10 threads)",
                "explanation", "With @ConcurrencyLimit(3) only 3 notifications can process simultaneously" +
                        "The remaining 7 orders queue and wait for permits to be available"
        ));





    }
}
