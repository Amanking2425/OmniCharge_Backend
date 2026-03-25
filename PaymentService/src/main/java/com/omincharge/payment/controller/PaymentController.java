package com.omincharge.payment.controller;

import com.omincharge.payment.dto.*;
import com.omincharge.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment", description = "Payment processing and transaction records")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Process a payment for a recharge")
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.processPayment(request));
    }

    @Operation(summary = "Get transaction by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getTransactionById(
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getTransactionById(id));
    }

    @Operation(summary = "Get transaction by reference number")
    @GetMapping("/ref/{ref}")
    public ResponseEntity<PaymentResponse> getTransactionByRef(
            @PathVariable String ref) {
        return ResponseEntity.ok(paymentService.getTransactionByRef(ref));
    }

    @Operation(summary = "Get all transactions for a user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getTransactionsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getTransactionsByUser(userId));
    }

    @Operation(summary = "Get all transactions for a recharge")
    @GetMapping("/recharge/{rechargeId}")
    public ResponseEntity<List<PaymentResponse>> getTransactionsByRecharge(
            @PathVariable Long rechargeId) {
        return ResponseEntity.ok(
            paymentService.getTransactionsByRecharge(rechargeId));
    }
    
    @Operation(summary = "Get all transactions — admin only")
    @GetMapping("/all")
    public ResponseEntity<List<PaymentResponse>> getAllTransactions() {
        return ResponseEntity.ok(paymentService.getAllTransactions());
    }
}