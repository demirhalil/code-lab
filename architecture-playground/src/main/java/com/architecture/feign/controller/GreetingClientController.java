package com.architecture.feign.controller;

import com.architecture.feign.client.GreetingClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that demonstrates FeignClient usage by calling the greeting service endpoints.
 */
@RestController
@RequestMapping("/api/client")
public class GreetingClientController {

    private final GreetingClient greetingClient;

    public GreetingClientController(GreetingClient greetingClient) {
        this.greetingClient = greetingClient;
    }

    @GetMapping("/hello")
    public String getHello() {
        return greetingClient.getHello();
    }

    @GetMapping("/how-are-you")
    public String getHowAreYou() {
        return greetingClient.getHowAreYou();
    }
}
