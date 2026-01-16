package com.architecture.feign.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller simulates an external service that provides greeting endpoints.
 * In a real-world scenario, this would be a separate microservice.
 */
@RestController
@RequestMapping("/api/greetings")
public class GreetingServiceController {

    @GetMapping("/hello")
    public String getHello() {
        return "Hello";
    }

    @GetMapping("/how-are-you")
    public String getHowAreYou() {
        return "How are you";
    }
}
