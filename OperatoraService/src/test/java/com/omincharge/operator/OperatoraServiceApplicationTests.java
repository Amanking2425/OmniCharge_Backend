package com.omincharge.operator;

import com.omincharge.operator.dto.*;
import com.omincharge.operator.entity.Operator;
import com.omincharge.operator.entity.RechargePlan;
import com.omincharge.operator.exception.OperatorAlreadyExistsException;
import com.omincharge.operator.exception.OperatorNotFoundException;
import com.omincharge.operator.exception.PlanNotFoundException;
import com.omincharge.operator.repository.OperatorRepository;
import com.omincharge.operator.repository.RechargePlanRepository;
import com.omincharge.operator.service.OperatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperatoraServiceApplicationTests {

    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private RechargePlanRepository planRepository;

    @InjectMocks
    private OperatorService operatorService;

    // ── Helper methods ────────────────────────────

    private Operator buildOperator(Long id, String name, String code) {
        Operator o = new Operator();
        o.setId(id);
        o.setName(name);
        o.setCode(code);
        o.setLogoUrl("http://logo.com/" + code + ".png");
        o.setStatus(Operator.Status.ACTIVE);
        return o;
    }

    private RechargePlan buildPlan(Long id, Operator operator,
            String name, BigDecimal amount, int validityDays) {
        RechargePlan p = new RechargePlan();
        p.setId(id);
        p.setOperator(operator);
        p.setName(name);
        p.setAmount(amount);
        p.setValidityDays(validityDays);
        p.setDataAllowance("1.5GB/day");
        p.setStatus(RechargePlan.Status.ACTIVE);
        return p;
    }

    // ── Operator Tests ────────────────────────────

    @Test
    void createOperator_Success() {
        OperatorRequest request = new OperatorRequest();
        request.setName("Airtel");
        request.setCode("AIRTEL");
        request.setLogoUrl("http://airtel.com/logo.png");

        when(operatorRepository.existsByCode("AIRTEL")).thenReturn(false);
        when(operatorRepository.existsByName("Airtel")).thenReturn(false);
        when(operatorRepository.save(any(Operator.class))).thenAnswer(inv -> {
            Operator o = inv.getArgument(0);
            return buildOperator(1L, o.getName(), o.getCode());
        });

        OperatorResponse response = operatorService.createOperator(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Airtel");
        assertThat(response.getCode()).isEqualTo("AIRTEL");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        verify(operatorRepository).save(any(Operator.class));
    }

    @Test
    void createOperator_ThrowsException_WhenCodeExists() {
        OperatorRequest request = new OperatorRequest();
        request.setName("Airtel");
        request.setCode("AIRTEL");

        when(operatorRepository.existsByCode("AIRTEL")).thenReturn(true);

        assertThrows(OperatorAlreadyExistsException.class,
                () -> operatorService.createOperator(request));

        verify(operatorRepository, never()).save(any());
    }

    @Test
    void createOperator_ThrowsException_WhenNameExists() {
        OperatorRequest request = new OperatorRequest();
        request.setName("Airtel");
        request.setCode("AIRTEL_NEW");

        when(operatorRepository.existsByCode("AIRTEL_NEW")).thenReturn(false);
        when(operatorRepository.existsByName("Airtel")).thenReturn(true);

        assertThrows(OperatorAlreadyExistsException.class,
                () -> operatorService.createOperator(request));

        verify(operatorRepository, never()).save(any());
    }

    @Test
    void getOperatorById_Success() {
        Operator operator = buildOperator(1L, "Airtel", "AIRTEL");

        when(operatorRepository.findById(1L)).thenReturn(Optional.of(operator));

        OperatorResponse response = operatorService.getOperatorById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Airtel");
        assertThat(response.getCode()).isEqualTo("AIRTEL");
    }

    @Test
    void getOperatorById_ThrowsException_WhenNotFound() {
        when(operatorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OperatorNotFoundException.class,
                () -> operatorService.getOperatorById(999L));
    }

    @Test
    void getAllOperators_ReturnsAll() {
        Operator op1 = buildOperator(1L, "Airtel", "AIRTEL");
        Operator op2 = buildOperator(2L, "Jio", "JIO");
        Operator op3 = buildOperator(3L, "Vi", "VI");

        when(operatorRepository.findAll()).thenReturn(List.of(op1, op2, op3));

        List<OperatorResponse> responses = operatorService.getAllOperators();

        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).getName()).isEqualTo("Airtel");
        assertThat(responses.get(1).getName()).isEqualTo("Jio");
        assertThat(responses.get(2).getName()).isEqualTo("Vi");
    }

    @Test
    void updateOperator_Success() {
        Operator existing = buildOperator(1L, "Airtel", "AIRTEL");

        OperatorRequest request = new OperatorRequest();
        request.setName("Airtel India");
        request.setCode("AIRTEL");
        request.setLogoUrl("http://airtel.com/newlogo.png");

        when(operatorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(operatorRepository.save(any(Operator.class))).thenAnswer(inv -> {
            return inv.getArgument(0);
        });

        OperatorResponse response = operatorService.updateOperator(1L, request);

        assertThat(response.getName()).isEqualTo("Airtel India");
        assertThat(response.getCode()).isEqualTo("AIRTEL");
    }

    @Test
    void updateOperator_ThrowsException_WhenNotFound() {
        OperatorRequest request = new OperatorRequest();
        request.setName("Airtel");
        request.setCode("AIRTEL");

        when(operatorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OperatorNotFoundException.class,
                () -> operatorService.updateOperator(999L, request));
    }

    // ── Plan Tests ────────────────────────────────

    @Test
    void createPlan_Success() {
        Operator operator = buildOperator(1L, "Airtel", "AIRTEL");

        PlanRequest request = new PlanRequest();
        request.setName("Airtel 299");
        request.setAmount(new BigDecimal("299.00"));
        request.setValidityDays(28);
        request.setDataAllowance("1.5GB/day");
        request.setDescription("Best plan");

        when(operatorRepository.findById(1L)).thenReturn(Optional.of(operator));
        when(planRepository.save(any(RechargePlan.class))).thenAnswer(inv -> {
            RechargePlan p = inv.getArgument(0);
            return buildPlan(1L, operator, p.getName(),
                    p.getAmount(), p.getValidityDays());
        });

        PlanResponse response = operatorService.createPlan(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Airtel 299");
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("299.00"));
        assertThat(response.getOperatorId()).isEqualTo(1L);
        assertThat(response.getOperatorName()).isEqualTo("Airtel");
    }

    @Test
    void createPlan_ThrowsException_WhenOperatorNotFound() {
        PlanRequest request = new PlanRequest();
        request.setName("Airtel 299");
        request.setAmount(new BigDecimal("299.00"));
        request.setValidityDays(28);

        when(operatorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OperatorNotFoundException.class,
                () -> operatorService.createPlan(999L, request));

        verify(planRepository, never()).save(any());
    }

    @Test
    void getPlanById_Success() {
        Operator operator = buildOperator(1L, "Airtel", "AIRTEL");
        RechargePlan plan = buildPlan(1L, operator,
                "Airtel 299", new BigDecimal("299.00"), 28);

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        PlanResponse response = operatorService.getPlanById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Airtel 299");
        assertThat(response.getValidityDays()).isEqualTo(28);
    }

    @Test
    void getPlanById_ThrowsException_WhenNotFound() {
        when(planRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PlanNotFoundException.class,
                () -> operatorService.getPlanById(999L));
    }

    @Test
    void getPlansByOperator_Success() {
        Operator operator = buildOperator(1L, "Airtel", "AIRTEL");
        RechargePlan plan1 = buildPlan(1L, operator,
                "Airtel 299", new BigDecimal("299.00"), 28);
        RechargePlan plan2 = buildPlan(2L, operator,
                "Airtel 599", new BigDecimal("599.00"), 56);

        when(operatorRepository.findById(1L)).thenReturn(Optional.of(operator));
        when(planRepository.findByOperatorId(1L)).thenReturn(List.of(plan1, plan2));

        List<PlanResponse> responses = operatorService.getPlansByOperator(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("Airtel 299");
        assertThat(responses.get(1).getName()).isEqualTo("Airtel 599");
    }

    @Test
    void getPlansByOperator_ThrowsException_WhenOperatorNotFound() {
        when(operatorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OperatorNotFoundException.class,
                () -> operatorService.getPlansByOperator(999L));
    }
}