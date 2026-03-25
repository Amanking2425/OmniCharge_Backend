package com.omincharge.payment;

import com.omincharge.payment.client.RechargeClient;
import com.omincharge.payment.dto.*;
import com.omincharge.payment.entity.Transaction;
import com.omincharge.payment.exception.InvalidPaymentException;
import com.omincharge.payment.exception.PaymentNotFoundException;
import com.omincharge.payment.repository.TransactionRepository;
import com.omincharge.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceApplicationTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RechargeClient rechargeClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentService paymentService;

    // ── Helper ────────────────────────────────────

    private Transaction buildTransaction(Long id,
            Transaction.Status status, Transaction.PaymentMethod method) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setRechargeId(1L);
        t.setUserId(1L);
        t.setAmount(new BigDecimal("299.00"));
        t.setPaymentMethod(method);
        t.setStatus(status);
        t.setTransactionRef("TXN-ABCDEF123456");
        return t;
    }

    private void injectRabbitValues() {
        ReflectionTestUtils.setField(paymentService,
            "paymentExchange", "payment.exchange");
        ReflectionTestUtils.setField(paymentService,
            "paymentRoutingKey", "payment.notification");
    }

    // ── Process Payment Tests ─────────────────────

    @Test
    void processPayment_Success() {
        injectRabbitValues();

        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(1L);
        request.setUserId(1L);
        request.setPaymentMethod("UPI");

        Map<String, Object> recharge = Map.of(
                "id", 1, "userId", 1,
                "status", "PENDING", "amount", "299.00");

        Transaction saved = buildTransaction(1L,
                Transaction.Status.PENDING, Transaction.PaymentMethod.UPI);
        Transaction completed = buildTransaction(1L,
                Transaction.Status.SUCCESS, Transaction.PaymentMethod.UPI);

        when(rechargeClient.getRechargeById(1L)).thenReturn(recharge);
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(saved)
                .thenReturn(completed);
        doNothing().when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(Object.class));

        PaymentResponse response = paymentService.processPayment(request);

        assertThat(response).isNotNull();
        assertThat(response.getRechargeId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("299.00"));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void processPayment_ThrowsException_WhenRechargeNotFound() {
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(999L);
        request.setUserId(1L);
        request.setPaymentMethod("UPI");

        when(rechargeClient.getRechargeById(999L))
                .thenThrow(new RuntimeException("Not found"));

        assertThrows(InvalidPaymentException.class,
                () -> paymentService.processPayment(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void processPayment_ThrowsException_WhenRechargeNotPending() {
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(1L);
        request.setUserId(1L);
        request.setPaymentMethod("UPI");

        Map<String, Object> recharge = Map.of(
                "id", 1, "userId", 1,
                "status", "COMPLETED", "amount", "299.00");

        when(rechargeClient.getRechargeById(1L)).thenReturn(recharge);

        assertThrows(InvalidPaymentException.class,
                () -> paymentService.processPayment(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void processPayment_ThrowsException_WhenWrongUser() {
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(1L);
        request.setUserId(2L);
        request.setPaymentMethod("UPI");

        Map<String, Object> recharge = Map.of(
                "id", 1, "userId", 1,
                "status", "PENDING", "amount", "299.00");

        when(rechargeClient.getRechargeById(1L)).thenReturn(recharge);

        assertThrows(InvalidPaymentException.class,
                () -> paymentService.processPayment(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void processPayment_ThrowsException_WhenInvalidPaymentMethod() {
        PaymentRequest request = new PaymentRequest();
        request.setRechargeId(1L);
        request.setUserId(1L);
        request.setPaymentMethod("CASH");

        Map<String, Object> recharge = Map.of(
                "id", 1, "userId", 1,
                "status", "PENDING", "amount", "299.00");

        when(rechargeClient.getRechargeById(1L)).thenReturn(recharge);

        assertThrows(InvalidPaymentException.class,
                () -> paymentService.processPayment(request));

        verify(transactionRepository, never()).save(any());
    }

    // ── Get Transaction Tests ─────────────────────

    @Test
    void getTransactionById_Success() {
        Transaction t = buildTransaction(1L,
                Transaction.Status.SUCCESS, Transaction.PaymentMethod.UPI);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(t));

        PaymentResponse response = paymentService.getTransactionById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getPaymentMethod()).isEqualTo("UPI");
        assertThat(response.getTransactionRef()).isEqualTo("TXN-ABCDEF123456");
    }

    @Test
    void getTransactionById_ThrowsException_WhenNotFound() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> paymentService.getTransactionById(999L));
    }

    @Test
    void getTransactionByRef_Success() {
        Transaction t = buildTransaction(1L,
                Transaction.Status.SUCCESS, Transaction.PaymentMethod.UPI);

        when(transactionRepository.findByTransactionRef("TXN-ABCDEF123456"))
                .thenReturn(Optional.of(t));

        PaymentResponse response = paymentService
                .getTransactionByRef("TXN-ABCDEF123456");

        assertThat(response.getTransactionRef()).isEqualTo("TXN-ABCDEF123456");
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
    }

    @Test
    void getTransactionByRef_ThrowsException_WhenNotFound() {
        when(transactionRepository.findByTransactionRef("TXN-INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> paymentService.getTransactionByRef("TXN-INVALID"));
    }

    @Test
    void getTransactionsByUser_ReturnsAll() {
        Transaction t1 = buildTransaction(1L,
                Transaction.Status.SUCCESS, Transaction.PaymentMethod.UPI);
        Transaction t2 = buildTransaction(2L,
                Transaction.Status.FAILED, Transaction.PaymentMethod.CREDIT_CARD);

        when(transactionRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(t1, t2));

        List<PaymentResponse> responses =
                paymentService.getTransactionsByUser(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getStatus()).isEqualTo("SUCCESS");
        assertThat(responses.get(1).getStatus()).isEqualTo("FAILED");
        assertThat(responses.get(1).getPaymentMethod()).isEqualTo("CREDIT_CARD");
    }

    @Test
    void getTransactionsByUser_ReturnsEmpty_WhenNoTransactions() {
        when(transactionRepository.findByUserIdOrderByCreatedAtDesc(999L))
                .thenReturn(List.of());

        List<PaymentResponse> responses =
                paymentService.getTransactionsByUser(999L);

        assertThat(responses).isEmpty();
    }

    @Test
    void getTransactionsByRecharge_ReturnsAll() {
        Transaction t1 = buildTransaction(1L,
                Transaction.Status.SUCCESS, Transaction.PaymentMethod.UPI);

        when(transactionRepository.findByRechargeId(1L))
                .thenReturn(List.of(t1));

        List<PaymentResponse> responses =
                paymentService.getTransactionsByRecharge(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRechargeId()).isEqualTo(1L);
    }

    @Test
    void getTransactionsByRecharge_ReturnsEmpty_WhenNone() {
        when(transactionRepository.findByRechargeId(999L))
                .thenReturn(List.of());

        List<PaymentResponse> responses =
                paymentService.getTransactionsByRecharge(999L);

        assertThat(responses).isEmpty();
    }
}