package com.dev.restaurant;

import java.util.List;

public record Restaurant(
        String id,
        String name,
        String cuisine,
        double rating,
        String address,
        List<String> menuItemIds
) {
}
