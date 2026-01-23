package com.dev.driver;

import com.dev.order.Order;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DriverController.class);
    private final DriverAssignmentService driverAssignmentService;

    public DriverController(final DriverAssignmentService driverAssignmentService) {
        this.driverAssignmentService = driverAssignmentService;
    }

    @PostMapping("/assign")
    public ResponseEntity<Map<String, Object>> assignDriver(
            @RequestParam String orderId
    ) {
        LOGGER.info("Api request: Assign driver for order {}", orderId);

        try{
            Order order = new Order(
                    orderId,
                    "customer-123",
                    "restaurant-001",
                    List.of("item-1", "item-2"),
                    new BigDecimal("25.99"),
                    "payment-123"
            );
            Driver driver = driverAssignmentService.assignDriver(order);

            return ResponseEntity.ok(
                    Map.of(
                            "orderId", orderId,
                            "driver", Map.of(
                                    "id", driver.id(),
                                    "name", driver.name(),
                                    "rating", driver.rating()
                            ),
                            "message", "Driver assigned successfully(possibly after retries"
                    )
            );
        }catch (Exception ex){
            LOGGER.error("Failed to assign driver after all retries:  {}", ex.getMessage());
            return ResponseEntity.status(503).body(Map.of(
                    "message", ex.getMessage(),
                    "message", ex.getMessage(),
                    "orderId", orderId
                    ));
        }
    }
}
