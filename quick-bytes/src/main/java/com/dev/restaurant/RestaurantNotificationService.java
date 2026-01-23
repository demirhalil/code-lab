package com.dev.restaurant;

import com.dev.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;

@Service
public class RestaurantNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantNotificationService.class);

    @ConcurrencyLimit(3)
    public void notifyRestaurant(Order order) {
        LocalTime start = LocalTime.now();
        logger.info("[CONCURRENT] Sending notification to restaurant for order: {} (Thread: {})", order.id(), Thread.currentThread().getName());
        simulateDelay(Duration.ofSeconds(2));

        LocalTime end = LocalTime.now();
        logger.info("[CONCURRENT] Notification sent to restaurant for order: {} (Thread: {}) (took {}ms)", order.id(), Thread.currentThread().getName(), Duration.between(start, end).getSeconds());

    }

    private void simulateDelay(final Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
