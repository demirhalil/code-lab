package com.dev.concurrency.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock")
@Getter
@Setter
@NoArgsConstructor
public class Stock {

    @Id
    private String productCode;

    private Integer quantity;

    @Version
    private Long version;

    public Stock(String productCode, Integer quantity) {
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public void reserve(Integer amount) {
        if (this.quantity < amount) {
            throw new RuntimeException("Insufficient stock for product: " + productCode);
        }
        this.quantity -= amount;
    }

    public void release(Integer amount) {
        this.quantity += amount;
    }
}
