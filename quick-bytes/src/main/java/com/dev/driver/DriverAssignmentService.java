package com.dev.driver;

import com.dev.order.Order;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DriverAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(DriverAssignmentService.class);
    private final List<Driver> drivers = new ArrayList<>();
    private final RetryTemplate retryTemplate;
    private final Random random = new Random();

    public DriverAssignmentService() {
        var retryPolicy = RetryPolicy.builder()
                .maxRetries(10)
                .delay(Duration.ofMillis(2000))
                .multiplier(1.5)
                .includes(NoDriversAvailableException.class)
                .build();
        retryTemplate = new RetryTemplate(retryPolicy);
    }

    public Driver assignDriver(final Order order) throws RetryException {
        logger.info("Attempting to assign driver for order {}", order.id());
        final AtomicInteger attempt  = new AtomicInteger(0);
        return retryTemplate.execute(() -> {
            int currentAttempt = attempt.incrementAndGet();
            logger.info("Attempt #{} to find available driver.", currentAttempt);

            if (random.nextDouble() > 0.5 || drivers.isEmpty()) {
                throw new NoDriversAvailableException("No drivers available in area. Will retry...");
            }

            final Driver assignedDriver = drivers.get(random.nextInt(drivers.size()));
            logger.info("Driver {} assigned to order {}", assignedDriver.name(), order.id());
            return assignedDriver;
        });
    }

    @PostConstruct
    private void initializeDrivers(){
        drivers.addAll(List.of(
            new Driver("1","John Doe", 4.8),
            new Driver("2","Jane Smith", 4.9),
            new Driver("3","Bob Johnson", 4.5),
            new Driver("4","Sarah Chen", 4.7),
            new Driver("5","Alex Johnson", 4.6),
            new Driver("6","Mike Robers", 4.4),
            new Driver("7","Maria Garcia", 4.5)
        ));
        logger.info("Drivers initialized: {}", drivers.size());
    }

}
