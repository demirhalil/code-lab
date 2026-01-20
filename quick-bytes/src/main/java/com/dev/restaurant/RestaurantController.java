package com.dev.restaurant;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RestaurantController.class);
    private final RestaurantService restaurantService;

    public RestaurantController(final RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
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
}
