package com.omincharge.payment.service;

import com.omincharge.payment.client.RechargeClient;
import com.omincharge.payment.dto.*;
import com.omincharge.payment.entity.Transaction;
import com.omincharge.payment.event.PaymentCompletedEvent;
import com.omincharge.payment.exception.InvalidPaymentException;
import com.omincharge.payment.exception.PaymentNotFoundException;
import com.omincharge.payment.repository.TransactionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final RechargeClient rechargeClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.payment}")
    private String paymentExchange;

    @Value("${rabbitmq.routing-key.payment}")
    private String paymentRoutingKey;

    public PaymentService(TransactionRepository transactionRepository,
                          RechargeClient rechargeClient,
                          RabbitTemplate rabbitTemplate) {
        this.transactionRepository = transactionRepository;
        this.rechargeClient        = rechargeClient;
        this.rabbitTemplate        = rabbitTemplate;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {

        // Validate recharge exists
        Map<String, Object> recharge;
        try {
            recharge = rechargeClient.getRechargeById(request.getRechargeId());
        } catch (Exception e) {
            throw new InvalidPaymentException(
                "Recharge not found with id: " + request.getRechargeId());
        }

        // Validate recharge status is PENDING
        String rechargeStatus = recharge.get("status").toString();
        if (!"PENDING".equals(rechargeStatus)) {
            throw new InvalidPaymentException(
                "Recharge is not in PENDING status. Current status: "
                + rechargeStatus);
        }

        // Validate user owns this recharge
        Object rechargeUserId = recharge.get("userId");
        if (!request.getUserId().equals(
                Long.valueOf(rechargeUserId.toString()))) {
            throw new InvalidPaymentException(
                "This recharge does not belong to user: " + request.getUserId());
        }

        // Validate payment method
        Transaction.PaymentMethod paymentMethod;
        try {
            paymentMethod = Transaction.PaymentMethod
                .valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentException(
                "Invalid payment method: " + request.getPaymentMethod()
                + ". Valid values: UPI, CREDIT_CARD, DEBIT_CARD, NET_BANKING, WALLET");
        }

        // Get amount from recharge
        BigDecimal amount = new BigDecimal(recharge.get("amount").toString());

        // Create transaction
        Transaction transaction = Transaction.builder()
                .rechargeId(request.getRechargeId())
                .userId(request.getUserId())
                .amount(amount)
                .paymentMethod(paymentMethod)
                .status(Transaction.Status.PENDING)
                .build();

        transaction = transactionRepository.save(transaction);

        // Simulate payment processing — confirm recharge
        try {
            rechargeClient.confirmRecharge(request.getRechargeId());
            transaction.setStatus(Transaction.Status.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(Transaction.Status.FAILED);
            transaction.setFailureReason("Recharge confirmation failed");
        }

        transaction = transactionRepository.save(transaction);

     // Publish payment event to RabbitMQ
        PaymentCompletedEvent event = new PaymentCompletedEvent(
            transaction.getId(),
            transaction.getRechargeId(),
            transaction.getUserId(),
            transaction.getAmount(),
            transaction.getPaymentMethod().name(),
            transaction.getStatus().name(),
            transaction.getTransactionRef()
        );

        try {
            rabbitTemplate.convertAndSend(paymentExchange, paymentRoutingKey, event);
        } catch (Exception e) {
            System.out.println("RabbitMQ is not running. Payment saved, but notification event was not sent: " + e.getMessage());
        }

        return toResponse(transaction);

    }

    public PaymentResponse getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new PaymentNotFoundException(
                    "Transaction not found with id: " + id));
    }

    public PaymentResponse getTransactionByRef(String ref) {
        return transactionRepository.findByTransactionRef(ref)
                .map(this::toResponse)
                .orElseThrow(() -> new PaymentNotFoundException(
                    "Transaction not found with ref: " + ref));
    }

    public List<PaymentResponse> getTransactionsByUser(Long userId) {
        return transactionRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                //.collect(Collectors.toList());
                .toList();
    }

    public List<PaymentResponse> getTransactionsByRecharge(Long rechargeId) {
        return transactionRepository
                .findByRechargeId(rechargeId)
                .stream()
                .map(this::toResponse)
                //.collect(Collectors.toList());
                .toList();
    }

    public List<PaymentResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::toResponse)
                //.collect(Collectors.toList());
                .toList();
    }
    
    private PaymentResponse toResponse(Transaction t) {
        return PaymentResponse.builder()
                .id(t.getId())
                .rechargeId(t.getRechargeId())
                .userId(t.getUserId())
                .amount(t.getAmount())
                .paymentMethod(t.getPaymentMethod().name())
                .status(t.getStatus().name())
                .transactionRef(t.getTransactionRef())
                .failureReason(t.getFailureReason())
                .createdAt(t.getCreatedAt())
                .build();
    }
}