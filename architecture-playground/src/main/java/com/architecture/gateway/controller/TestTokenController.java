package com.architecture.gateway.controller;

import com.architecture.gateway.util.JwtTokenGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test endpoint to generate JWT tokens for testing the gateway.
 * This should be removed or secured in production.
 */
@RestController
@RequestMapping("/api/auth/test")
public class TestTokenController {

    private final JwtTokenGenerator tokenGenerator;

    public TestTokenController(JwtTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> generateTestToken(
            @RequestParam(defaultValue = "testuser") String username,
            @RequestParam(required = false) List<String> roles) {
        
        if (roles == null || roles.isEmpty()) {
            roles = List.of("USER");
        }
        
        String token = tokenGenerator.generateToken(username, roles);
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);
        response.put("roles", String.join(",", roles));
        response.put("usage", "Use this token in Authorization header: Bearer <token>");
        
        return ResponseEntity.ok(response);
    }
}
