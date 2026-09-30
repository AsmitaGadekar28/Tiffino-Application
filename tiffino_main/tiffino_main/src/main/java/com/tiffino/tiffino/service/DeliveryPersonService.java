package com.tiffino.tiffino.service;
import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.entity.DeliveryPerson;
import com.tiffino.tiffino.entity.Order;
import com.tiffino.tiffino.entity.request.DeliveryPersonPasswordRequest;
import com.tiffino.tiffino.exception.CustomException;
import com.tiffino.tiffino.repository.DeliveryPersonRepo;
import com.tiffino.tiffino.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class DeliveryPersonService {

    @Autowired
    private DeliveryPersonRepo deliveryPersonRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OrderRepo orderRepo;

    //  Update password using OTP
  /**  public String updatePasswordWithOtp(DeliveryPersonPasswordRequest request) {
        DeliveryPerson dp = deliveryPersonRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Delivery Person not found with email: " + request.getEmail()));

        if (dp.getOtp() == null || !dp.getOtp().equals(request.getOtp())) {
            throw new CustomException("Invalid OTP");
        }

        // Update password
        dp.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Clear OTP once used
        dp.setOtp(null);

        deliveryPersonRepo.save(dp);

        return " Password updated successfully!";
    }**/

    // Pick up order
 /**   public String pickUpOrder(Long orderId, String email) {
        DeliveryPerson dp = deliveryPersonRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException("Delivery partner not found"));

        if (!dp.getIsActive() || !dp.getIsAvailable()) {
            throw new CustomException("Delivery partner not available");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        order.setStatus("OUT_OF_DELIVERY");
        dp.setIsAvailable(false);

        orderRepo.save(order);
        order.setAssignedAt(LocalDateTime.now());
        deliveryPersonRepo.save(dp);

        return "Out of Delivery";
    }
**/

 // ================= PICK UP ORDER =================
 public String pickUpOrder(Long orderId, String email) {
     DeliveryPerson dp = deliveryPersonRepo.findByEmail(email)
             .orElseThrow(() -> new CustomException("Delivery partner not found"));

     Order order = orderRepo.findById(orderId)
             .orElseThrow(() -> new CustomException("Order not found"));

     // ✅ Check if this delivery person is assigned to the same CloudKitchen
     if (order.getCloudKitchen() == null || dp.getCloudKitchen() == null ||
             !Objects.equals(order.getCloudKitchen().getCloudKitchenId(), dp.getCloudKitchen().getCloudKitchenId())) {
         throw new CustomException("You are not assigned to this CloudKitchen!");
     }

     // ✅ Check if this delivery person is assigned to this specific order (if applicable)
     if (order.getDeliveryPerson() == null || !order.getDeliveryPerson().getDeliveryPersonId().equals(dp.getDeliveryPersonId())) {
         throw new CustomException("You are not assigned to this order!");
     }

     // ✅ Only allow pickup if order is in PREPARING state
     if (!"ASSIGNED".equalsIgnoreCase(order.getStatus())) {
         throw new CustomException("Order can only be picked up if it's in ASSIGNED state!");
     }

     // ✅ Update order status
     order.setStatus("OUT_FOR_DELIVERY");
     order.setPickUpAt(LocalDateTime.now());

     orderRepo.save(order);
     return "Order #" + order.getOrderId() + " is now OUT FOR DELIVERY.";
 }


    // ================= DELIVER ORDER =================
    public String deliverOrder(Long orderId, String email) {
        DeliveryPerson dp = deliveryPersonRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException("Delivery partner not found"));

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        // ✅ Verify same CloudKitchen
        if (order.getCloudKitchen() == null || dp.getCloudKitchen() == null ||
                !Objects.equals(order.getCloudKitchen().getCloudKitchenId(), dp.getCloudKitchen().getCloudKitchenId())) {
            throw new CustomException("You are not assigned to this CloudKitchen!");
        }

        // ✅ Verify delivery person assigned to this order
        if (order.getDeliveryPerson() == null || !order.getDeliveryPerson().getDeliveryPersonId().equals(dp.getDeliveryPersonId())) {
            throw new CustomException("You are not assigned to this order!");
        }

        // ✅ Order can only be delivered if it's out for delivery
        if (!"OUT_FOR_DELIVERY".equalsIgnoreCase(order.getStatus())) {
            throw new CustomException("Order can only be delivered if it's OUT FOR DELIVERY!");
        }

        // ✅ Update order status and timestamps
        order.setStatus("DELIVERED");
        order.setDeliveredAt(LocalDateTime.now());
        orderRepo.save(order);

        // ✅ Make delivery person available again
        dp.setIsAvailable(true);
        deliveryPersonRepo.save(dp);

        return "Order #" + order.getOrderId() + " marked as DELIVERED successfully.";
    }


}
