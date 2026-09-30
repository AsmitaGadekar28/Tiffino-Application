package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
}
