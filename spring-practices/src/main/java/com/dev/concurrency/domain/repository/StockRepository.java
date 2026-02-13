package com.dev.concurrency.domain.repository;

import com.dev.concurrency.domain.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
}
