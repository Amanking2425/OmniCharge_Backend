package com.omincharge.recharge.service;

import com.omincharge.recharge.client.OperatorClient;
import com.omincharge.recharge.dto.*;
import com.omincharge.recharge.entity.RechargeHistory;
import com.omincharge.recharge.entity.RechargeRequest;
import com.omincharge.recharge.exception.InvalidRechargeException;
import com.omincharge.recharge.exception.RechargeNotFoundException;
import com.omincharge.recharge.repository.RechargeHistoryRepository;
import com.omincharge.recharge.repository.RechargeRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RechargeService {

    private final RechargeRequestRepository rechargeRepository;
    private final RechargeHistoryRepository historyRepository;
    private final OperatorClient operatorClient;

    public RechargeService(RechargeRequestRepository rechargeRepository,
                           RechargeHistoryRepository historyRepository,
                           OperatorClient operatorClient) {
        this.rechargeRepository = rechargeRepository;
        this.historyRepository  = historyRepository;
        this.operatorClient     = operatorClient;
    }

    @Transactional
    public RechargeResponse initiateRecharge(RechargeInitiateRequest request) {

        // Validate operator exists
        Map<String, Object> operator;
        try {
            operator = operatorClient.getOperatorById(request.getOperatorId());
        } catch (Exception e) {
            throw new InvalidRechargeException(
                "Operator not found with id: " + request.getOperatorId());
        }

        // Validate plan exists and belongs to operator
        Map<String, Object> plan;
        try {
            plan = operatorClient.getPlanById(request.getPlanId());
        } catch (Exception e) {
            throw new InvalidRechargeException(
                "Plan not found with id: " + request.getPlanId());
        }

        // Validate plan belongs to the requested operator
        Object planOperatorId = plan.get("operatorId");
        if (planOperatorId == null ||
            !request.getOperatorId().equals(
                Long.valueOf(planOperatorId.toString()))) {
            throw new InvalidRechargeException(
                "Plan does not belong to the specified operator");
        }

        // Get amount from plan
        BigDecimal amount = new BigDecimal(plan.get("amount").toString());

        // Create recharge request
        RechargeRequest recharge = RechargeRequest.builder()
                .userId(request.getUserId())
                .operatorId(request.getOperatorId())
                .planId(request.getPlanId())
                .phoneNumber(request.getPhoneNumber())
                .amount(amount)
                .status(RechargeRequest.Status.PENDING)
                .build();

        recharge = rechargeRepository.save(recharge);

        // Save history entry
        saveHistory(recharge.getId(), "PENDING",
            "Recharge initiated for " + request.getPhoneNumber());

        return toResponse(recharge);
    }

    @Transactional
    public RechargeResponse confirmRecharge(Long rechargeId) {
        RechargeRequest recharge = rechargeRepository.findById(rechargeId)
                .orElseThrow(() -> new RechargeNotFoundException(
                    "Recharge not found with id: " + rechargeId));

        if (recharge.getStatus() != RechargeRequest.Status.PENDING) {
            throw new InvalidRechargeException(
                "Recharge is not in PENDING status. Current status: "
                + recharge.getStatus());
        }

        // Simulate processing
        recharge.setStatus(RechargeRequest.Status.PROCESSING);
        rechargeRepository.save(recharge);
        saveHistory(rechargeId, "PROCESSING", "Recharge is being processed");

        // Simulate completion
        recharge.setStatus(RechargeRequest.Status.COMPLETED);
        rechargeRepository.save(recharge);
        saveHistory(rechargeId, "COMPLETED",
            "Recharge completed successfully for " + recharge.getPhoneNumber());

        return toResponse(recharge);
    }

    public RechargeResponse getRechargeById(Long id) {
        return rechargeRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RechargeNotFoundException(
                    "Recharge not found with id: " + id));
    }

    public List<RechargeResponse> getRechargesByUser(Long userId) {
        return rechargeRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void saveHistory(Long rechargeId, String status, String message) {
        RechargeHistory history = RechargeHistory.builder()
                .rechargeId(rechargeId)
                .status(status)
                .message(message)
                .build();
        historyRepository.save(history);
    }

    public List<RechargeResponse> getAllRecharges() {
        return rechargeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    private RechargeResponse toResponse(RechargeRequest r) {
        return RechargeResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .operatorId(r.getOperatorId())
                .planId(r.getPlanId())
                .phoneNumber(r.getPhoneNumber())
                .amount(r.getAmount())
                .status(r.getStatus().name())
                .failureReason(r.getFailureReason())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}