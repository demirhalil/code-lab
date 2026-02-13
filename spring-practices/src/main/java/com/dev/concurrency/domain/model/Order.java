package com.dev.concurrency.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    @Version
    private Long version;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    public Order(String customerId) {
        this.customerId = customerId;
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.totalAmount = BigDecimal.ZERO;
    }

    public void addItem(String productCode, Integer quantity, BigDecimal price) {
        OrderItem item = new OrderItem(productCode, quantity, price);
        this.items.add(item);
        this.totalAmount = this.totalAmount.add(price.multiply(BigDecimal.valueOf(quantity)));
    }

    public void markAsValidated() {
        if (this.status == OrderStatus.CREATED) {
            this.status = OrderStatus.VALIDATED;
        }
    }

    public void markAsCompleted() {
        if (this.status == OrderStatus.VALIDATED) {
            this.status = OrderStatus.COMPLETED;
        }
    }

    public void markAsFailed(String reason) {
        this.status = OrderStatus.FAILED;
    }
}
