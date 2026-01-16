package com.architecture.gateway.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check and monitoring endpoints for the API Gateway.
 */
@RestController
@RequestMapping("/gateway/health")
public class GatewayHealthController {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;

    public GatewayHealthController(CircuitBreakerRegistry circuitBreakerRegistry,
                                  RateLimiterRegistry rateLimiterRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "API Gateway");
        health.put("timestamp", java.time.Instant.now());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/circuit-breaker")
    public ResponseEntity<Map<String, Object>> circuitBreakerStatus() {
        Map<String, Object> status = new HashMap<>();
        
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            CircuitBreaker.State state = cb.getState();
            io.github.resilience4j.circuitbreaker.CircuitBreaker.Metrics metrics = cb.getMetrics();
            
            Map<String, Object> cbStatus = new HashMap<>();
            cbStatus.put("state", state.name());
            cbStatus.put("failureRate", metrics.getFailureRate());
            cbStatus.put("numberOfSuccessfulCalls", metrics.getNumberOfSuccessfulCalls());
            cbStatus.put("numberOfFailedCalls", metrics.getNumberOfFailedCalls());
            cbStatus.put("numberOfNotPermittedCalls", metrics.getNumberOfNotPermittedCalls());
            
            status.put(cb.getName(), cbStatus);
        });
        
        return ResponseEntity.ok(status);
    }

    @GetMapping("/rate-limiter")
    public ResponseEntity<Map<String, Object>> rateLimiterStatus() {
        Map<String, Object> status = new HashMap<>();
        
        rateLimiterRegistry.getAllRateLimiters().forEach(rl -> {
            RateLimiter.Metrics metrics = rl.getMetrics();
            
            Map<String, Object> rlStatus = new HashMap<>();
            rlStatus.put("availablePermissions", metrics.getAvailablePermissions());
            rlStatus.put("numberOfWaitingThreads", metrics.getNumberOfWaitingThreads());
            
            status.put(rl.getName(), rlStatus);
        });
        
        return ResponseEntity.ok(status);
    }
}
