package com.omincharge.recharge;

import com.omincharge.recharge.client.OperatorClient;
import com.omincharge.recharge.dto.*;
import com.omincharge.recharge.entity.RechargeHistory;
import com.omincharge.recharge.entity.RechargeRequest;
import com.omincharge.recharge.exception.InvalidRechargeException;
import com.omincharge.recharge.exception.RechargeNotFoundException;
import com.omincharge.recharge.repository.RechargeHistoryRepository;
import com.omincharge.recharge.repository.RechargeRequestRepository;
import com.omincharge.recharge.service.RechargeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RechargeServiceApplicationTests {

    @Mock
    private RechargeRequestRepository rechargeRepository;

    @Mock
    private RechargeHistoryRepository historyRepository;

    @Mock
    private OperatorClient operatorClient;

    @InjectMocks
    private RechargeService rechargeService;

    // ── Helper ────────────────────────────────────

    private RechargeRequest buildRecharge(Long id, RechargeRequest.Status status) {
        RechargeRequest r = new RechargeRequest();
        r.setId(id);
        r.setUserId(1L);
        r.setOperatorId(1L);
        r.setPlanId(1L);
        r.setPhoneNumber("9876543210");
        r.setAmount(new BigDecimal("299.00"));
        r.setStatus(status);
        return r;
    }

    // ── Initiate Recharge Tests ───────────────────

    @Test
    void initiateRecharge_Success() {
        RechargeInitiateRequest request = new RechargeInitiateRequest();
        request.setUserId(1L);
        request.setOperatorId(1L);
        request.setPlanId(1L);
        request.setPhoneNumber("9876543210");

        Map<String, Object> operator = Map.of("id", 1, "name", "Airtel");
        Map<String, Object> plan = Map.of(
                "id", 1,
                "operatorId", 1,
                "amount", "299.00",
                "name", "Airtel 299");

        when(operatorClient.getOperatorById(1L)).thenReturn(operator);
        when(operatorClient.getPlanById(1L)).thenReturn(plan);
        when(rechargeRepository.save(any(RechargeRequest.class))).thenAnswer(inv -> {
            RechargeRequest r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });
        when(historyRepository.save(any(RechargeHistory.class))).thenReturn(null);

        RechargeResponse response = rechargeService.initiateRecharge(request);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getOperatorId()).isEqualTo(1L);
        assertThat(response.getPlanId()).isEqualTo(1L);
        assertThat(response.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("299.00"));
        assertThat(response.getStatus()).isEqualTo("PENDING");
        verify(rechargeRepository).save(any(RechargeRequest.class));
    }

    @Test
    void initiateRecharge_ThrowsException_WhenOperatorNotFound() {
        RechargeInitiateRequest request = new RechargeInitiateRequest();
        request.setUserId(1L);
        request.setOperatorId(999L);
        request.setPlanId(1L);
        request.setPhoneNumber("9876543210");

        when(operatorClient.getOperatorById(999L))
                .thenThrow(new RuntimeException("Not found"));

        assertThrows(InvalidRechargeException.class,
                () -> rechargeService.initiateRecharge(request));

        verify(rechargeRepository, never()).save(any());
    }

    @Test
    void initiateRecharge_ThrowsException_WhenPlanNotFound() {
        RechargeInitiateRequest request = new RechargeInitiateRequest();
        request.setUserId(1L);
        request.setOperatorId(1L);
        request.setPlanId(999L);
        request.setPhoneNumber("9876543210");

        Map<String, Object> operator = Map.of("id", 1, "name", "Airtel");

        when(operatorClient.getOperatorById(1L)).thenReturn(operator);
        when(operatorClient.getPlanById(999L))
                .thenThrow(new RuntimeException("Not found"));

        assertThrows(InvalidRechargeException.class,
                () -> rechargeService.initiateRecharge(request));

        verify(rechargeRepository, never()).save(any());
    }

    @Test
    void initiateRecharge_ThrowsException_WhenPlanDoesNotBelongToOperator() {
        RechargeInitiateRequest request = new RechargeInitiateRequest();
        request.setUserId(1L);
        request.setOperatorId(1L);
        request.setPlanId(3L);
        request.setPhoneNumber("9876543210");

        Map<String, Object> operator = Map.of("id", 1, "name", "Airtel");
        Map<String, Object> plan = Map.of(
                "id", 3,
                "operatorId", 2,
                "amount", "239.00",
                "name", "Jio 239");

        when(operatorClient.getOperatorById(1L)).thenReturn(operator);
        when(operatorClient.getPlanById(3L)).thenReturn(plan);

        assertThrows(InvalidRechargeException.class,
                () -> rechargeService.initiateRecharge(request));

        verify(rechargeRepository, never()).save(any());
    }

    // ── Confirm Recharge Tests ────────────────────

    @Test
    void confirmRecharge_Success() {
        RechargeRequest recharge = buildRecharge(1L, RechargeRequest.Status.PENDING);

        when(rechargeRepository.findById(1L)).thenReturn(Optional.of(recharge));
        when(rechargeRepository.save(any(RechargeRequest.class))).thenReturn(recharge);
        when(historyRepository.save(any(RechargeHistory.class))).thenReturn(null);

        RechargeResponse response = rechargeService.confirmRecharge(1L);

        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        verify(rechargeRepository, times(2)).save(any(RechargeRequest.class));
    }

    @Test
    void confirmRecharge_ThrowsException_WhenAlreadyCompleted() {
        RechargeRequest recharge = buildRecharge(1L, RechargeRequest.Status.COMPLETED);

        when(rechargeRepository.findById(1L)).thenReturn(Optional.of(recharge));

        assertThrows(InvalidRechargeException.class,
                () -> rechargeService.confirmRecharge(1L));

        verify(rechargeRepository, never()).save(any());
    }

    @Test
    void confirmRecharge_ThrowsException_WhenAlreadyFailed() {
        RechargeRequest recharge = buildRecharge(1L, RechargeRequest.Status.FAILED);

        when(rechargeRepository.findById(1L)).thenReturn(Optional.of(recharge));

        assertThrows(InvalidRechargeException.class,
                () -> rechargeService.confirmRecharge(1L));
    }

    @Test
    void confirmRecharge_ThrowsException_WhenNotFound() {
        when(rechargeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RechargeNotFoundException.class,
                () -> rechargeService.confirmRecharge(999L));
    }

    // ── Get Recharge Tests ────────────────────────

    @Test
    void getRechargeById_Success() {
        RechargeRequest recharge = buildRecharge(1L, RechargeRequest.Status.COMPLETED);

        when(rechargeRepository.findById(1L)).thenReturn(Optional.of(recharge));

        RechargeResponse response = rechargeService.getRechargeById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("299.00"));
    }

    @Test
    void getRechargeById_ThrowsException_WhenNotFound() {
        when(rechargeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RechargeNotFoundException.class,
                () -> rechargeService.getRechargeById(999L));
    }

    @Test
    void getRechargesByUser_ReturnsAll() {
        RechargeRequest r1 = buildRecharge(1L, RechargeRequest.Status.COMPLETED);
        RechargeRequest r2 = buildRecharge(2L, RechargeRequest.Status.PENDING);

        when(rechargeRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(r1, r2));

        List<RechargeResponse> responses = rechargeService.getRechargesByUser(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getStatus()).isEqualTo("COMPLETED");
        assertThat(responses.get(1).getStatus()).isEqualTo("PENDING");
    }

    @Test
    void getRechargesByUser_ReturnsEmpty_WhenNoRecharges() {
        when(rechargeRepository.findByUserIdOrderByCreatedAtDesc(999L))
                .thenReturn(List.of());

        List<RechargeResponse> responses = rechargeService.getRechargesByUser(999L);

        assertThat(responses).isEmpty();
    }
}