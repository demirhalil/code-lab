package com.dev.restaurant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);
    private final DataLoader dataLoader;
    private final Random random = new Random();

    public RestaurantService(final DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public List<MenuItem> getMenuFromPartner(String restaurantid) {
        logger.info("Getting Menu from restaurant Partner API: {}", restaurantid);

        // Simulate flaky external API (40% failure rate)
        if (random.nextDouble() < 0.4) {
            logger.warn("Partner API failed");
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
