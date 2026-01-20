package com.dev.restaurant;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataLoader implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
    private final JsonMapper mapper;

    public DataLoader(final JsonMapper mapper) {
        this.mapper = mapper;
    }

    private final Map<String, Restaurant> restaurants = new ConcurrentHashMap<>();
    private final Map<String, MenuItem> menuItems = new ConcurrentHashMap<>();

    @Override
    public void run(final String... args) throws Exception {
        LOGGER.info("Loading Restaurant Data from JSON files...");
        loadData();
        LOGGER.info("Restaurant Data Loaded Successfully");
    }

    private void loadData() {
        try {
            // Load restaurants
            var restaurantResource = new ClassPathResource("data/restaurants.json");
            List<Restaurant> restaurants = mapper.readValue(restaurantResource.getInputStream(), new TypeReference<>() {
            });
            restaurants.forEach(r -> this.restaurants.put(r.id(), r));
            LOGGER.info(" -> Loaded {} restaurants", restaurants.size());

            // Load menu items
            ClassPathResource menuItemResource = new ClassPathResource("data/menu-items.json");
            List<MenuItem> menuItems = mapper.readValue(menuItemResource.getInputStream(), new TypeReference<>() {
            });
            menuItems.forEach(m -> this.menuItems.put(m.id(), m));
            LOGGER.info(" -> Loaded {} menu items", menuItems.size());

        } catch (JacksonException e) {
            LOGGER.error("Failed to load data from JSON files", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Restaurant> getRestaurants() {
        return restaurants;
    }

    public Map<String, MenuItem> getMenuItems() {
        return menuItems;
    }

    public Restaurant getRestaurant(String id) {
        return restaurants.get(id);
    }
    public MenuItem getMenuItem(String id) {
        return menuItems.get(id);
    }
}
