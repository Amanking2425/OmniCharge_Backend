package com.omincharge.recharge.controller;

import com.omincharge.recharge.dto.*;
import com.omincharge.recharge.service.RechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recharges")
@Tag(name = "Recharge", description = "Mobile recharge initiation and tracking")
public class RechargeController {

    private final RechargeService rechargeService;

    public RechargeController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    @Operation(summary = "Initiate a new recharge")
    @PostMapping("/initiate")
    public ResponseEntity<RechargeResponse> initiateRecharge(
            @Valid @RequestBody RechargeInitiateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rechargeService.initiateRecharge(request));
    }

    @Operation(summary = "Confirm a pending recharge")
    @PostMapping("/{id}/confirm")
    public ResponseEntity<RechargeResponse> confirmRecharge(
            @PathVariable Long id) {
        return ResponseEntity.ok(rechargeService.confirmRecharge(id));
    }

    @Operation(summary = "Get recharge by ID")
    @GetMapping("/{id}")
    public ResponseEntity<RechargeResponse> getRechargeById(
            @PathVariable Long id) {
        return ResponseEntity.ok(rechargeService.getRechargeById(id));
    }

    @Operation(summary = "Get all recharges for a user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RechargeResponse>> getRechargesByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(rechargeService.getRechargesByUser(userId));
    }
    
    @Operation(summary = "Get all recharges — admin only")
    @GetMapping("/all")
    public ResponseEntity<List<RechargeResponse>> getAllRecharges() {
        return ResponseEntity.ok(rechargeService.getAllRecharges());
    }
}