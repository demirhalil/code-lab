# API Gateway Implementation

This package implements a production-ready API Gateway with comprehensive features for securing and managing microservices.

## Features

### 🔐 JWT Authentication & Authorization
- Validates JWT tokens before requests reach microservices
- Extracts user information (username, roles) from tokens
- Configurable public endpoints that bypass authentication
- Secure token validation with expiration checks

### ⚡ Rate Limiting / Throttling
- Prevents API abuse and ensures fair resource usage
- Configurable requests per minute per client
- IP-based client identification
- Rate limit headers in responses (`X-RateLimit-Remaining`, `X-RateLimit-Reset`)

### 🔄 Circuit Breaker
- Prevents cascading failures when services are down
- Configurable failure thresholds and timeouts
- Automatic recovery with half-open state
- Tracks slow calls and failures

### 📊 Request/Response Logging
- Request ID tracking for distributed tracing
- Comprehensive request/response logging
- MDC (Mapped Diagnostic Context) for log correlation
- Performance metrics (request duration)

### 🛡️ Production-Ready Patterns
- **CORS Configuration**: Cross-origin resource sharing support
- **Global Exception Handling**: Consistent error responses
- **Health Checks**: Gateway health and component status endpoints
- **Metrics & Monitoring**: Actuator endpoints for Prometheus
- **Request ID Propagation**: Track requests across services

## Architecture

### Component Structure

```
com.architecture.gateway/
├── config/
│   ├── CircuitBreakerConfig.java      # Circuit breaker configuration
│   ├── FilterOrderConfig.java          # Filter execution order
│   └── GatewayConfig.java              # CORS and gateway settings
├── controller/
│   ├── GatewayHealthController.java    # Health check endpoints
│   └── TestTokenController.java        # JWT token generator (testing)
├── exception/
│   └── GlobalExceptionHandler.java     # Global error handling
├── filter/
│   ├── JwtAuthenticationFilter.java    # JWT validation filter
│   ├── RateLimitingFilter.java         # Rate limiting filter
│   └── RequestLoggingFilter.java       # Request/response logging
├── security/
│   └── JwtUtil.java                     # JWT utility functions
└── util/
    └── JwtTokenGenerator.java          # Token generation utility
```

### Request Flow

```
1. Client Request
   ↓
2. RequestLoggingFilter (Adds Request ID, logs request)
   ↓
3. RateLimitingFilter (Checks rate limits)
   ↓
4. JwtAuthenticationFilter (Validates JWT token)
   ↓
5. Target Microservice
   ↓
6. Response Logging (Logs response, adds headers)
   ↓
7. Client Response
```

## Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
# JWT Configuration
jwt:
  secret: your-secret-key  # Use strong secret in production
  expiration: 3600000      # Token expiration in milliseconds

# Gateway Security
gateway:
  security:
    enabled: true
    public-endpoints: /api/auth/**,/actuator/**,/health

# Rate Limiting
gateway:
  rate-limit:
    enabled: true
    requests-per-minute: 60

# Circuit Breaker
gateway:
  circuit-breaker:
    enabled: true
    failure-rate-threshold: 50
    wait-duration-in-open-state: 60
```

## Usage

### 1. Generate a Test JWT Token

```bash
# Get a test token
curl http://localhost:8080/api/auth/test/token?username=testuser

# Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "roles": "USER",
  "usage": "Use this token in Authorization header: Bearer <token>"
}
```

### 2. Make Authenticated Requests

```bash
# Use the token in Authorization header
curl -H "Authorization: Bearer <your-token>" \
     http://localhost:8080/api/greetings/hello
```

### 3. Check Gateway Health

```bash
# Gateway health
curl http://localhost:8080/gateway/health

# Circuit breaker status
curl http://localhost:8080/gateway/health/circuit-breaker

# Rate limiter status
curl http://localhost:8080/gateway/health/rate-limiter
```

### 4. Monitor Metrics

```bash
# Prometheus metrics
curl http://localhost:8080/actuator/prometheus

# Health endpoint
curl http://localhost:8080/actuator/health
```

## Filter Execution Order

Filters are executed in the following order (configured in `FilterOrderConfig`):

1. **RequestLoggingFilter** (Order: HIGHEST_PRECEDENCE)
   - Adds request ID
   - Logs incoming request

2. **RateLimitingFilter** (Order: HIGHEST_PRECEDENCE + 1)
   - Checks rate limits
   - Blocks if limit exceeded

3. **JwtAuthenticationFilter** (Order: HIGHEST_PRECEDENCE + 2)
   - Validates JWT token
   - Extracts user information

## Rate Limiting

### How It Works

- Uses Resilience4j RateLimiter
- Tracks requests per client (IP address)
- Default: 60 requests per minute
- Returns `429 Too Many Requests` when limit exceeded

### Response Headers

- `X-RateLimit-Remaining`: Number of requests remaining
- `X-RateLimit-Reset`: Timestamp when limit resets
- `Retry-After`: Seconds to wait before retrying

## Circuit Breaker

### States

1. **CLOSED**: Normal operation, requests pass through
2. **OPEN**: Circuit is open, requests fail fast
3. **HALF_OPEN**: Testing if service recovered

### Configuration

- **Failure Rate Threshold**: 50% (default)
- **Sliding Window Size**: 10 calls
- **Wait Duration**: 60 seconds before trying half-open
- **Slow Call Threshold**: 60 seconds

## JWT Authentication

### Token Structure

```json
{
  "sub": "username",
  "roles": ["USER", "ADMIN"],
  "iat": 1234567890,
  "exp": 1234571490
}
```

### Public Endpoints

Endpoints that don't require authentication (configurable):
- `/api/auth/**` - Authentication endpoints
- `/actuator/**` - Monitoring endpoints
- `/health` - Health checks
- `/gateway/health/**` - Gateway health

## Production Considerations

### 1. JWT Secret
- Use a strong, randomly generated secret (at least 32 characters)
- Store in secure configuration management (Vault, AWS Secrets Manager)
- Rotate secrets periodically

### 2. Rate Limiting
- Consider using Redis for distributed rate limiting
- Implement different limits for different user tiers
- Use user ID instead of IP for authenticated users

### 3. Circuit Breaker
- Monitor circuit breaker metrics
- Set appropriate thresholds based on service SLAs
- Implement fallback responses

### 4. Logging
- Use structured logging (JSON format)
- Send logs to centralized logging system (ELK, Splunk)
- Include request IDs in all logs for tracing

### 5. Security
- Use HTTPS in production
- Implement IP whitelisting if needed
- Add request size limits
- Implement DDoS protection

### 6. Monitoring
- Set up alerts for circuit breaker state changes
- Monitor rate limit violations
- Track authentication failures
- Monitor request latencies

## Testing

### Test Rate Limiting

```bash
# Make multiple rapid requests
for i in {1..70}; do
  curl http://localhost:8080/api/greetings/hello
done
# After 60 requests, you'll get 429 Too Many Requests
```

### Test JWT Validation

```bash
# Request without token (should fail)
curl http://localhost:8080/api/greetings/hello
# Response: 401 Unauthorized

# Request with invalid token
curl -H "Authorization: Bearer invalid-token" \
     http://localhost:8080/api/greetings/hello
# Response: 401 Unauthorized
```

## Dependencies

- **Spring Boot Web**: MVC framework
- **Resilience4j**: Circuit breaker and rate limiting
- **JJWT**: JWT token handling
- **Spring Boot Actuator**: Monitoring and metrics
- **Redis** (optional): Distributed rate limiting

## Future Enhancements

- [ ] Service discovery integration (Eureka, Consul)
- [ ] Load balancing
- [ ] Request/response transformation
- [ ] API versioning
- [ ] Request routing based on headers
- [ ] Distributed tracing (Zipkin, Jaeger)
- [ ] API key authentication
- [ ] OAuth2 integration
- [ ] Request caching
- [ ] WebSocket support
