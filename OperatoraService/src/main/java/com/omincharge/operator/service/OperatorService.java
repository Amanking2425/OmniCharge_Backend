package com.omincharge.operator.service;

import com.omincharge.operator.dto.*;
import com.omincharge.operator.entity.Operator;
import com.omincharge.operator.entity.RechargePlan;
import com.omincharge.operator.exception.OperatorAlreadyExistsException;
import com.omincharge.operator.exception.OperatorNotFoundException;
import com.omincharge.operator.exception.PlanNotFoundException;
import com.omincharge.operator.repository.OperatorRepository;
import com.omincharge.operator.repository.RechargePlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperatorService {

    private final OperatorRepository operatorRepository;
    private final RechargePlanRepository planRepository;

    public OperatorService(OperatorRepository operatorRepository,
                           RechargePlanRepository planRepository) {
        this.operatorRepository = operatorRepository;
        this.planRepository     = planRepository;
    }

    // ── Operator Methods ──────────────────────────────

    public List<OperatorResponse> getAllOperators() {
        return operatorRepository.findAll()
                .stream()
                .map(this::toOperatorResponse)
                //.collect(Collectors.toList());
                .toList();
    }

    public OperatorResponse getOperatorById(Long id) {
        return operatorRepository.findById(id)
                .map(this::toOperatorResponse)
                .orElseThrow(() ->
                    new OperatorNotFoundException("Operator not found with id: " + id));
    }

    public OperatorResponse createOperator(OperatorRequest request) {
        if (operatorRepository.existsByCode(request.getCode())) {
            throw new OperatorAlreadyExistsException(
                "Operator already exists with code: " + request.getCode());
        }
        if (operatorRepository.existsByName(request.getName())) {
            throw new OperatorAlreadyExistsException(
                "Operator already exists with name: " + request.getName());
        }

        Operator operator = Operator.builder()
                .name(request.getName())
                .code(request.getCode())
                .logoUrl(request.getLogoUrl())
                .status(Operator.Status.ACTIVE)
                .build();

        return toOperatorResponse(operatorRepository.save(operator));
    }

    public OperatorResponse updateOperator(Long id, OperatorRequest request) {
        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() ->
                    new OperatorNotFoundException("Operator not found id: " + id));

        operator.setName(request.getName());
        operator.setCode(request.getCode());
        operator.setLogoUrl(request.getLogoUrl());

        return toOperatorResponse(operatorRepository.save(operator));
    }

    // ── Plan Methods ──────────────────────────────────

    public List<PlanResponse> getPlansByOperator(Long operatorId) {
        operatorRepository.findById(operatorId)
                .orElseThrow(() ->
                    new OperatorNotFoundException(
                        "Operator not found whose id: " + operatorId));

        return planRepository.findByOperatorId(operatorId)
                .stream()
                .map(this::toPlanResponse)
                //.collect(Collectors.toList());
                .toList();
    }

    public PlanResponse getPlanById(Long planId) {
        return planRepository.findById(planId)
                .map(this::toPlanResponse)
                .orElseThrow(() ->
                    new PlanNotFoundException("Plan not found with id: " + planId));
    }

    public PlanResponse createPlan(Long operatorId, PlanRequest request) {
        Operator operator = operatorRepository.findById(operatorId)
                .orElseThrow(() ->
                    new OperatorNotFoundException(
                        "Operator not found where id is: " + operatorId));

        RechargePlan plan = RechargePlan.builder()
                .operator(operator)
                .name(request.getName())
                .amount(request.getAmount())
                .validityDays(request.getValidityDays())
                .dataAllowance(request.getDataAllowance())
                .description(request.getDescription())
                .status(RechargePlan.Status.ACTIVE)
                .build();

        return toPlanResponse(planRepository.save(plan));
    }

    // ── Mappers ───────────────────────────────────────

    private OperatorResponse toOperatorResponse(Operator o) {
        return OperatorResponse.builder()
                .id(o.getId())
                .name(o.getName())
                .code(o.getCode())
                .logoUrl(o.getLogoUrl())
                .status(o.getStatus().name())
                .createdAt(o.getCreatedAt())
                .build();
    }

    private PlanResponse toPlanResponse(RechargePlan p) {
        return PlanResponse.builder()
                .id(p.getId())
                .operatorId(p.getOperator().getId())
                .operatorName(p.getOperator().getName())
                .name(p.getName())
                .amount(p.getAmount())
                .validityDays(p.getValidityDays())
                .dataAllowance(p.getDataAllowance())
                .description(p.getDescription())
                .status(p.getStatus().name())
                .build();
    }
}