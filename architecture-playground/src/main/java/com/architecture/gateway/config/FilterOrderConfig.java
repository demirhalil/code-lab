package com.architecture.gateway.config;

import com.architecture.gateway.filter.JwtAuthenticationFilter;
import com.architecture.gateway.filter.RateLimitingFilter;
import com.architecture.gateway.filter.RequestLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Configuration to set the order of filters.
 * Filters are executed in the order defined here.
 */
@Configuration
public class FilterOrderConfig {

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter(
            RequestLoggingFilter filter) {
        FilterRegistrationBean<RequestLoggingFilter> registration = 
            new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // First filter
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter(
            RateLimitingFilter filter) {
        FilterRegistrationBean<RateLimitingFilter> registration = 
            new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // Second filter
        return registration;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = 
            new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 2); // Third filter
        return registration;
    }
}
