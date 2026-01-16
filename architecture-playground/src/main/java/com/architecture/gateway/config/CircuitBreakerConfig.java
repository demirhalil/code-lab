package com.architecture.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Circuit breaker configuration to prevent cascading failures.
 * When a service is down or slow, the circuit breaker opens and fails fast.
 */
@Configuration
public class CircuitBreakerConfig {

    @Value("${gateway.circuit-breaker.enabled:true}")
    private boolean circuitBreakerEnabled;

    @Value("${gateway.circuit-breaker.failure-rate-threshold:50}")
    private float failureRateThreshold;

    @Value("${gateway.circuit-breaker.wait-duration-in-open-state:60}")
    private int waitDurationInOpenState;

    @Value("${gateway.circuit-breaker.sliding-window-size:10}")
    private int slidingWindowSize;

    @Value("${gateway.circuit-breaker.minimum-number-of-calls:5}")
    private int minimumNumberOfCalls;

    @Value("${gateway.circuit-breaker.slow-call-rate-threshold:100}")
    private float slowCallRateThreshold;

    @Value("${gateway.circuit-breaker.slow-call-duration-threshold:60}")
    private int slowCallDurationThreshold;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig config = 
            io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationInOpenState))
                .slidingWindowSize(slidingWindowSize)
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .slowCallRateThreshold(slowCallRateThreshold)
                .slowCallDurationThreshold(Duration.ofSeconds(slowCallDurationThreshold))
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(Exception.class)
                .build();

        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public CircuitBreaker apiGatewayCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("apiGatewayCircuitBreaker");
    }
}
