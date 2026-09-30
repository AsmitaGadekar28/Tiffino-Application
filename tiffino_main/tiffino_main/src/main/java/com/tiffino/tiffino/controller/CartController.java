package com.tiffino.tiffino.controller;

import com.tiffino.tiffino.dto.*;
import com.tiffino.tiffino.entity.response.CartUpdateResponse;
import com.tiffino.tiffino.exception.CustomException;
import com.tiffino.tiffino.service.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/cart")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // -------------------- ADD TO CART --------------------
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestHeader("Authorization") String token,
            @RequestBody AddToCartRequest request
    ) throws Exception {
        cartService.addToCart(token, request);
        return ResponseEntity.ok("Added to cart successfully");
    }

    // -------------------- VIEW CART --------------------
    @GetMapping("/view")
    public ResponseEntity<CartResponse> viewCart(
            @RequestHeader("Authorization") String token
    ) throws Exception {
        return ResponseEntity.ok(cartService.viewCart(token));
    }

    // -------------------- ADD ALLERGIES --------------------
    /** @PostMapping("/add-allergies")
    public ResponseEntity<CartResponse> addAllergies(
     @RequestHeader("Authorization") String token,
     @RequestBody List<String> allergies
     ) throws Exception {
     return ResponseEntity.ok(cartService.addAllergies(token, allergies));
     }**/

    /**  @PostMapping("/add-allergies")
    public ResponseEntity<CartResponse> addAllergies(
     @RequestHeader("Authorization") String token,
     @RequestBody Map<String, List<String>> body
     ) throws Exception {
     List<String> allergies = body.get("allergies");
     return ResponseEntity.ok(cartService.addAllergies(token, allergies));
     }**/

    // -------------------- UPDATE ALLERGIES --------------------
    /** @PostMapping("/allergies")
    public ResponseEntity<CartResponse> updateAllergies(
     @RequestHeader("Authorization") String token,
     @RequestBody AllergiesUpdateRequest request) throws Exception {

     CartResponse updatedCart = cartService.updateAllergies(token, request);
     return ResponseEntity.ok(updatedCart);
     }**/

// -------------------- UPDATE ALLERGIES --------------------
    @PostMapping("/allergies")
    public ResponseEntity<CartUpdateResponse> updateAllergies(
            @RequestHeader("Authorization") String token,
            @RequestBody AllergiesUpdateRequest request) {
        try {
            CartUpdateResponse response = cartService.updateAllergies(token, request);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            // 400 Bad Request with proper message
            return ResponseEntity.badRequest()
                    .body(new CartUpdateResponse(null, e.getMessage()));
        } catch (Exception e) {
            // 500 Internal Server Error
            return ResponseEntity.status(500)
                    .body(new CartUpdateResponse(null, "Something went wrong"));
        }
    }


    // -------------------- UPDATE CART QUANTITY --------------------
    @PutMapping("/updateQty")
    public ResponseEntity<CartResponse> updateCartQty(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateCartQtyRequest request
    ) throws Exception {
        return ResponseEntity.ok(cartService.updateCartQty(token, request));
    }

    // -------------------- REMOVE FROM CART --------------------
    @PostMapping("/remove")
    public ResponseEntity<CartResponse> removeFromCart(
            @RequestHeader("Authorization") String token,
            @RequestBody RemoveFromCartRequest request
    ) throws Exception {
        return ResponseEntity.ok(cartService.removeFromCart(token, request));
    }

    // -------------------- CLEAR CART --------------------
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(
            @RequestHeader("Authorization") String token
    ) throws Exception {
        cartService.clearCart(token);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
