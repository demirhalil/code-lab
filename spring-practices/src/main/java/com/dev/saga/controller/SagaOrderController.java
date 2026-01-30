package com.dev.saga.controller;

import com.dev.saga.model.Order;
import com.dev.saga.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/api/saga/orders")
public class SagaOrderController {
    private final OrderService orderService;

    public SagaOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestParam String product, @RequestParam int quantity, @RequestParam BigDecimal price) {
        return orderService.createOrder(product, quantity, price);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable UUID id) {
        return orderService.getOrder(id);
    }

    @GetMapping
    public Collection<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}
