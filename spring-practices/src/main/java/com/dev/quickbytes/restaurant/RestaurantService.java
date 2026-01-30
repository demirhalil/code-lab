package com.dev.quickbytes.restaurant;

import com.dev.quickbytes.restaurant.DataLoader;
import com.dev.quickbytes.restaurant.Restaurant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);
    private final DataLoader dataLoader;
    private final Random random = new Random();
    private int maxRetries = 4;

    public RestaurantService(final DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    // @Retryable defaults to retry on any exception if no exception types are specified.
    // That means any runtime or checked exception thrown from this method will trigger a retry.
    // Be explicit if only certain failures should be retried.
    @Retryable(
            maxRetries = 4,
            includes = RestaurantApiException.class,
            delay = 1000,
            multiplier = 2
    )
    public List<MenuItem> getMenuFromPartner(String restaurantid) {
        logger.info("Getting Menu from restaurant Partner API: {}", restaurantid);

        // Simulate flaky external API (40% failure rate)
        if (random.nextDouble() < 0.4) {
            logger.warn("Partner API failed. Will retry again...");
            this.maxRetries = this.maxRetries - 1;
            logger.info("Number of retries: {}", this.maxRetries);
            throw new RestaurantApiException("Partner API failed");
        }

        simulateDelay(Duration.ofMillis(200));

        Restaurant restaurant = dataLoader.getRestaurant(restaurantid);
        if (restaurant == null) {
            throw new RestaurantApiException("Restaurant not found");
        }

        List<MenuItem> menu = restaurant.menuItemIds().stream()
                .map(dataLoader::getMenuItem)
                .filter(item -> item != null && item.available())
                .toList();

        logger.info("Menu retrieved {} menu items from: {}", menu.size(), restaurant.name());
        return menu;
    }

    public List<Restaurant> findAll() {
        return dataLoader.getRestaurants().values().stream().toList();
    }

    private void simulateDelay(Duration delay) {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
