package com.tiffino.tiffino.controller;

import com.tiffino.tiffino.config.JwtUtil;
import com.tiffino.tiffino.service.InvoiceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final JwtUtil jwtUtil;

    @GetMapping("/viewinvoice/{orderId}")
    public void viewInvoice(@PathVariable Long orderId,
                            @RequestHeader("Authorization") String token,
                            HttpServletResponse response) throws IOException {

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String userEmail = jwtUtil.extractUsername(jwt); // extract logged-in user's email

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=invoice_" + orderId + ".pdf");

        invoiceService.generateInvoice(orderId, userEmail, response);
    }
}
