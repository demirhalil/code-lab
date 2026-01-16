package com.architecture.gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

/**
 * Request logging filter that adds request ID tracking and logs request/response details.
 * Useful for tracing requests across microservices in production.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "requestId";
    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Generate or extract request ID
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        
        // Add request ID to MDC for logging context
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        
        // Add request ID to response header
        response.setHeader(REQUEST_ID_HEADER, requestId);
        
        // Record start time
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        
        // Wrap request/response for logging
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        try {
            // Log incoming request
            logRequest(wrappedRequest, requestId);
            
            // Continue filter chain
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            
        } finally {
            // Log response
            long duration = System.currentTimeMillis() - startTime;
            logResponse(wrappedResponse, requestId, duration);
            
            // Copy response body back to original response
            wrappedResponse.copyBodyToResponse();
            
            // Clear MDC
            MDC.clear();
        }
    }

    private void logRequest(HttpServletRequest request, String requestId) {
        if (logger.isDebugEnabled()) {
            logger.debug("Incoming request - ID: {}, Method: {}, URI: {}, IP: {}, Headers: {}", 
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                getHeadersAsString(request));
        } else {
            logger.info("Incoming request - ID: {}, Method: {}, URI: {}", 
                requestId, request.getMethod(), request.getRequestURI());
        }
    }

    private void logResponse(ContentCachingResponseWrapper response, String requestId, long duration) {
        int status = response.getStatus();
        
        if (logger.isDebugEnabled()) {
            logger.debug("Outgoing response - ID: {}, Status: {}, Duration: {}ms, Headers: {}", 
                requestId, status, duration, getResponseHeadersAsString(response));
        } else {
            logger.info("Outgoing response - ID: {}, Status: {}, Duration: {}ms", 
                requestId, status, duration);
        }
    }

    private String getHeadersAsString(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append(", ");
        });
        return headers.toString();
    }

    private String getResponseHeadersAsString(ContentCachingResponseWrapper response) {
        StringBuilder headers = new StringBuilder();
        response.getHeaderNames().forEach(headerName -> {
            headers.append(headerName).append(": ").append(response.getHeader(headerName)).append(", ");
        });
        return headers.toString();
    }
}
