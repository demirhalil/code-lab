//package com.dev.concurrency;
//
//import com.dev.concurrency.application.dto.OrderRequest;
//import com.dev.concurrency.domain.model.OrderStatus;
//import com.dev.concurrency.domain.model.Stock;
//import com.dev.concurrency.domain.repository.OrderRepository;
//import com.dev.concurrency.domain.repository.StockRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.ResponseEntity;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.awaitility.Awaitility.await;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class OrderSystemIntegrationTest {
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private StockRepository stockRepository;
//
//    @BeforeEach
//    void setup() {
//        orderRepository.deleteAll();
//        stockRepository.deleteAll();
//        stockRepository.save(new Stock("PROD1", 10));
//    }
//
//    @Test
//    void shouldProcessOrderSuccessfullyThroughSaga() {
//        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest("PROD1", 2, new BigDecimal("100.00"));
//        OrderRequest request = new OrderRequest("CUST1", List.of(item));
//
//        ResponseEntity<UUID> response = restTemplate.postForEntity("/api/orders", request, UUID.class);
//
//        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
//        UUID orderId = response.getBody();
//        assertThat(orderId).isNotNull();
//
//        // Wait for Async Saga & Outbox processing
//        await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
//            var order = orderRepository.findById(orderId).orElseThrow();
//            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
//
//            var stock = stockRepository.findById("PROD1").orElseThrow();
//            assertThat(stock.getQuantity()).isEqualTo(8);
//        });
//    }
//
//    @Test
//    void shouldFailOrderWhenStockIsInsufficient() {
//        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest("PROD1", 20, new BigDecimal("100.00"));
//        OrderRequest request = new OrderRequest("CUST1", List.of(item));
//
//        ResponseEntity<UUID> response = restTemplate.postForEntity("/api/orders", request, UUID.class);
//
//        UUID orderId = response.getBody();
//
//        await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
//            var order = orderRepository.findById(orderId).orElseThrow();
//            assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
//        });
//    }
//}
