package com.dev.concurrency.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String productCode;

    private Integer quantity;

    private BigDecimal price;

    public OrderItem(String productCode, Integer quantity, BigDecimal price) {
        this.productCode = productCode;
        this.quantity = quantity;
        this.price = price;
    }
}
