package com.architecture.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * FeignClient interface for calling the greeting service endpoints.
 * The 'url' attribute points to the same application for this example.
 * In a real-world scenario, this would point to a different service URL.
 */
@FeignClient(name = "greeting-service", url = "http://localhost:8080")
public interface GreetingClient {

    @GetMapping("/api/greetings/hello")
    String getHello();

    @GetMapping("/api/greetings/how-are-you")
    String getHowAreYou();
}
