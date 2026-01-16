package com.architecture.gateway.filter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

/**
 * Rate limiting filter to prevent API abuse and ensure fair resource usage.
 * Uses Resilience4j RateLimiter for throttling requests.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    private static final String RATE_LIMITER_NAME = "apiGatewayRateLimiter";
    private static final String X_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
    private static final String X_RATE_LIMIT_RESET = "X-RateLimit-Reset";

    private final RateLimiterRegistry rateLimiterRegistry;
    private RateLimiter rateLimiter;

    @Value("${gateway.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${gateway.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${gateway.rate-limit.timeout-duration:0}")
    private long timeoutDuration;

    public RateLimitingFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        if (rateLimitEnabled) {
            // Create or get rate limiter configuration
            io.github.resilience4j.ratelimiter.RateLimiterConfig config = 
                io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                    .limitRefreshPeriod(Duration.ofMinutes(1))
                    .limitForPeriod(requestsPerMinute)
                    .timeoutDuration(Duration.ofMillis(timeoutDuration))
                    .build();

            rateLimiter = rateLimiterRegistry.rateLimiter(RATE_LIMITER_NAME, config);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = getClientIdentifier(request);
        
        try {
            // Acquire permission from rate limiter
            RateLimiter.Metrics metrics = rateLimiter.getMetrics();
            
            // Try to acquire permission (non-blocking check)
            if (rateLimiter.acquirePermission()) {
                // Add rate limit headers
                int remaining = (int) (metrics.getAvailablePermissions());
                // Reset time is approximately 60 seconds from now (1 minute window)
                long resetTime = System.currentTimeMillis() + 60000;
                
                response.setHeader(X_RATE_LIMIT_REMAINING, String.valueOf(remaining));
                response.setHeader(X_RATE_LIMIT_RESET, String.valueOf(resetTime));
                
                logger.debug("Rate limit check passed for client: {}", clientId);
                filterChain.doFilter(request, response);
            } else {
                logger.warn("Rate limit exceeded for client: {} on path: {}", 
                    clientId, request.getRequestURI());
                sendRateLimitExceededResponse(response);
            }
            
        } catch (RequestNotPermitted e) {
            logger.warn("Rate limit request not permitted for client: {}", clientId);
            sendRateLimitExceededResponse(response);
        } catch (Exception e) {
            logger.error("Error in rate limiting filter: {}", e.getMessage(), e);
            // On error, allow request to proceed (fail open)
            filterChain.doFilter(request, response);
        }
    }

    private String getClientIdentifier(HttpServletRequest request) {
        // Use IP address as client identifier
        String ipAddress = request.getRemoteAddr();
        String forwardedFor = request.getHeader("X-Forwarded-For");
        
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            // Take the first IP in case of multiple proxies
            ipAddress = forwardedFor.split(",")[0].trim();
        }
        
        return ipAddress;
    }

    private void sendRateLimitExceededResponse(HttpServletResponse response) 
            throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.setHeader("Retry-After", "60"); // Retry after 60 seconds
        
        response.getWriter().write(String.format(
            "{\"error\": \"Rate Limit Exceeded\", \"message\": \"Too many requests. Please try again later.\", \"timestamp\": \"%s\"}", 
            java.time.Instant.now()
        ));
    }
}
