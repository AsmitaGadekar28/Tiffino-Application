package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.CloudKitchen;
import com.tiffino.tiffino.entity.Order;
import com.tiffino.tiffino.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findAllByOrderByOrderTimeDesc();

    List<Order> findAllByUserOrderByOrderTimeDesc(User user);

    List<Order> findAllByCloudKitchenOrderByOrderTimeDesc(CloudKitchen managerKitchen);

    Optional<Order> findByOrderIdAndCloudKitchen(Long orderId, CloudKitchen kitchen);

    List<Order> findByCloudKitchen(CloudKitchen cloudKitchen);

 Optional<Order> findByOrderIdAndUser_Email(Long orderId, String email);

 // ✅ Fetch last delivered order by user email
 Order findTopByUser_EmailAndStatusOrderByDeliveredAtDesc(String email, String status);


 }


