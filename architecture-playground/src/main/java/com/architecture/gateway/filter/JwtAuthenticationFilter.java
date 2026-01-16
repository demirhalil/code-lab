package com.architecture.gateway.filter;

import com.architecture.gateway.security.JwtUtil;
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
import java.util.List;

/**
 * Filter to validate JWT tokens before requests reach microservices.
 * This filter intercepts all requests and validates JWT tokens for protected endpoints.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Value("${gateway.security.public-endpoints:/api/auth/**,/actuator/**,/health}")
    private String publicEndpoints;

    @Value("${gateway.security.enabled:true}")
    private boolean securityEnabled;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Skip JWT validation for public endpoints
        if (!securityEnabled || isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("Missing or invalid Authorization header for path: {}", requestPath);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        try {
            String token = authHeader.substring(BEARER_PREFIX.length());
            
            if (!jwtUtil.validateToken(token)) {
                logger.warn("Invalid or expired JWT token for path: {}", requestPath);
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
                return;
            }

            // Extract user information and add to request attributes for downstream services
            String username = jwtUtil.extractUsername(token);
            List<String> roles = jwtUtil.extractRoles(token);
            
            request.setAttribute("username", username);
            request.setAttribute("roles", roles);
            request.setAttribute("jwt-token", token);
            
            logger.debug("JWT validation successful for user: {} on path: {}", username, requestPath);
            
        } catch (Exception e) {
            logger.error("Error validating JWT token: {}", e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token validation failed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        if (publicEndpoints == null || publicEndpoints.isEmpty()) {
            return false;
        }
        
        String[] endpoints = publicEndpoints.split(",");
        for (String endpoint : endpoints) {
            String pattern = endpoint.trim().replace("**", ".*");
            if (path.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) 
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"error\": \"%s\", \"message\": \"%s\", \"timestamp\": \"%s\"}", 
            status.getReasonPhrase(), 
            message,
            java.time.Instant.now()
        ));
    }
}
