package com.tiffino.tiffino.controller;
import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.entity.request.DeliveryPersonPasswordRequest;
import com.tiffino.tiffino.service.DeliveryPersonService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveryPerson")
@SecurityRequirement(name = "bearerAuth")
public class DeliveryPersonController {

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    @Autowired
    private  JwtUtil jwtUtil;


    //  Update password with OTP
   /** @PostMapping("/updatePassword")
    public ResponseEntity<String> updatePasswordWithOtp(@RequestBody DeliveryPersonPasswordRequest request) {
        String response = deliveryPersonService.updatePasswordWithOtp(request);
        return ResponseEntity.ok(response);
    }**/

    // Pick up order
    @PostMapping("/pickup/{orderId}")
    public ResponseEntity<String> pickUpOrder(@PathVariable Long orderId,
                                              @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractUsername(token.substring(7));
        String message = deliveryPersonService.pickUpOrder(orderId, email);
        return ResponseEntity.ok(message);
    }

    // Deliver order
    @PostMapping("/deliver/{orderId}")
    public ResponseEntity<String> deliverOrder(@PathVariable Long orderId,
                                               @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractUsername(token.substring(7));
        String message = deliveryPersonService.deliverOrder(orderId, email);
        return ResponseEntity.ok(message);
    }

}
