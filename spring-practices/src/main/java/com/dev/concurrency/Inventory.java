package com.dev.concurrency;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Inventory {

    @Id
    @GeneratedValue
    private Long id;

    private String productCode;

    private int stock;

    @Version
    private Long version;

    public Inventory() {}

    public Inventory(String productCode, int stock) {
        this.productCode = productCode;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public String getProductCode() { return productCode; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public Long getVersion() { return version; }
}
