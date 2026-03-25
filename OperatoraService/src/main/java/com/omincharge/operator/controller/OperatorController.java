package com.omincharge.operator.controller;

import com.omincharge.operator.dto.*;
import com.omincharge.operator.service.OperatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operators")
@Tag(name = "Operator", description = "Telecom operators and recharge plans management")
public class OperatorController {

    private final OperatorService operatorService;

    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @Operation(summary = "Get all operators")
    @GetMapping
    public ResponseEntity<List<OperatorResponse>> getAllOperators() {
        return ResponseEntity.ok(operatorService.getAllOperators());
    }

    @Operation(summary = "Get operator by ID")
    @GetMapping("/{id}")
    public ResponseEntity<OperatorResponse> getOperatorById(
            @PathVariable Long id) {
        return ResponseEntity.ok(operatorService.getOperatorById(id));
    }

    @Operation(summary = "Create new operator")
    @PostMapping
    public ResponseEntity<OperatorResponse> createOperator(
            @Valid @RequestBody OperatorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(operatorService.createOperator(request));
    }

    @Operation(summary = "Update operator")
    @PutMapping("/{id}")
    public ResponseEntity<OperatorResponse> updateOperator(
            @PathVariable Long id,
            @Valid @RequestBody OperatorRequest request) {
        return ResponseEntity.ok(operatorService.updateOperator(id, request));
    }

    @Operation(summary = "Get all plans for an operator")
    @GetMapping("/{id}/plans")
    public ResponseEntity<List<PlanResponse>> getPlansByOperator(
            @PathVariable Long id) {
        return ResponseEntity.ok(operatorService.getPlansByOperator(id));
    }

    @Operation(summary = "Get plan by ID")
    @GetMapping("/plans/{planId}")
    public ResponseEntity<PlanResponse> getPlanById(
            @PathVariable Long planId) {
        return ResponseEntity.ok(operatorService.getPlanById(planId));
    }

    @Operation(summary = "Create plan for operator")
    @PostMapping("/{id}/plans")
    public ResponseEntity<PlanResponse> createPlan(
            @PathVariable Long id,
            @Valid @RequestBody PlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(operatorService.createPlan(id, request));
    }
}