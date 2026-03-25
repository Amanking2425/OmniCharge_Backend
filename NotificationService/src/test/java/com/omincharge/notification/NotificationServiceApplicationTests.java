package com.omincharge.notification;

import com.omincharge.notification.event.PaymentCompletedEvent;
import com.omincharge.notification.event.RechargeCompletedEvent;
import com.omincharge.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceApplicationTests {

    @InjectMocks
    private NotificationService notificationService;

    // ── Recharge Notification Tests ───────────────

    @Test
    void sendRechargeNotification_Success() {
        RechargeCompletedEvent event = new RechargeCompletedEvent();
        event.setRechargeId(1L);
        event.setUserId(1L);
        event.setPhoneNumber("9876543210");
        event.setAmount(new BigDecimal("299.00"));
        event.setStatus("COMPLETED");

        assertDoesNotThrow(() ->
            notificationService.sendRechargeNotification(event));
    }

    @Test
    void sendRechargeNotification_WithFailedStatus() {
        RechargeCompletedEvent event = new RechargeCompletedEvent();
        event.setRechargeId(2L);
        event.setUserId(1L);
        event.setPhoneNumber("9876543210");
        event.setAmount(new BigDecimal("199.00"));
        event.setStatus("FAILED");

        assertDoesNotThrow(() ->
            notificationService.sendRechargeNotification(event));
    }

    @Test
    void sendRechargeNotification_WithNullAmount() {
        RechargeCompletedEvent event = new RechargeCompletedEvent();
        event.setRechargeId(3L);
        event.setUserId(1L);
        event.setPhoneNumber("9876543210");
        event.setAmount(null);
        event.setStatus("FAILED");

        assertDoesNotThrow(() ->
            notificationService.sendRechargeNotification(event));
    }

    // ── Payment Notification Tests ────────────────

    @Test
    void sendPaymentNotification_Success() {
        PaymentCompletedEvent event = new PaymentCompletedEvent();
        event.setTransactionId(1L);
        event.setRechargeId(1L);
        event.setUserId(1L);
        event.setAmount(new BigDecimal("299.00"));
        event.setPaymentMethod("UPI");
        event.setStatus("SUCCESS");
        event.setTransactionRef("TXN-ABCDEF123456");

        assertDoesNotThrow(() ->
            notificationService.sendPaymentNotification(event));
    }

    @Test
    void sendPaymentNotification_WithFailedStatus() {
        PaymentCompletedEvent event = new PaymentCompletedEvent();
        event.setTransactionId(2L);
        event.setRechargeId(2L);
        event.setUserId(1L);
        event.setAmount(new BigDecimal("599.00"));
        event.setPaymentMethod("CREDIT_CARD");
        event.setStatus("FAILED");
        event.setTransactionRef("TXN-XYZXYZ789012");

        assertDoesNotThrow(() ->
            notificationService.sendPaymentNotification(event));
    }

    @Test
    void sendPaymentNotification_WithAllPaymentMethods() {
        String[] methods = {"UPI", "CREDIT_CARD", "DEBIT_CARD",
                            "NET_BANKING", "WALLET"};

        for (String method : methods) {
            PaymentCompletedEvent event = new PaymentCompletedEvent();
            event.setTransactionId(1L);
            event.setRechargeId(1L);
            event.setUserId(1L);
            event.setAmount(new BigDecimal("299.00"));
            event.setPaymentMethod(method);
            event.setStatus("SUCCESS");
            event.setTransactionRef("TXN-TEST123456");

            assertDoesNotThrow(() ->
                notificationService.sendPaymentNotification(event),
                "Should not throw for payment method: " + method);
        }
    }

    @Test
    void sendRechargeNotification_WithDifferentAmounts() {
        BigDecimal[] amounts = {
            new BigDecimal("99.00"),
            new BigDecimal("199.00"),
            new BigDecimal("299.00"),
            new BigDecimal("599.00")
        };

        for (BigDecimal amount : amounts) {
            RechargeCompletedEvent event = new RechargeCompletedEvent();
            event.setRechargeId(1L);
            event.setUserId(1L);
            event.setPhoneNumber("9876543210");
            event.setAmount(amount);
            event.setStatus("COMPLETED");

            assertDoesNotThrow(() ->
                notificationService.sendRechargeNotification(event),
                "Should not throw for amount: " + amount);
        }
    }
}