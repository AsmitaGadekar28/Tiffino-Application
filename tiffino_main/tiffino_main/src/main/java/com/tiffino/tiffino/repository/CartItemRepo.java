package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {
}
