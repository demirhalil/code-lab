package com.dev.concurrency.interfaces.rest;

import com.dev.concurrency.application.dto.OrderRequest;
import com.dev.concurrency.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Entry point for Order Processing.
     * Demonstrates MDC for Correlation-ID tracking.
     */
    @PostMapping
    public ResponseEntity<UUID> placeOrder(@RequestBody OrderRequest request,
                                         @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put("correlationId", correlationId);
        
        try {
            log.info("Received order request for customer: {}", request.customerId());
            UUID orderId = orderService.placeOrder(request);
            return ResponseEntity.accepted().body(orderId);
        } finally {
            // MDC should be cleared if not using a Filter/Interceptor, 
            // though MdcTaskDecorator handles it for async threads.
            MDC.remove("correlationId");
        }
    }
}
