
/**
import com.tiffino.tiffino.entity.Cart;
import com.tiffino.tiffino.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);

    List<Cart> findAllByUser(User user);



}
**/
package com.tiffino.tiffino.repository;


import com.tiffino.tiffino.entity.Cart;
import com.tiffino.tiffino.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Long> {

    // Find cart by user
    Optional<Cart> findByUser(User user);

    // Find all carts of a user
    List<Cart> findAllByUser(User user);




    // Optional: If you still want to query by userId
    List<Cart> findAllByUser_UserId(Long userId);
}
