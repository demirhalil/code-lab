package com.dev.concurrency.domain.repository;

import com.dev.concurrency.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
