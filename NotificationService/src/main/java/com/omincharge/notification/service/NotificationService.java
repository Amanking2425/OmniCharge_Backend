package com.omincharge.notification.service;

import com.omincharge.notification.event.PaymentCompletedEvent;
import com.omincharge.notification.event.RechargeCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log =
        LoggerFactory.getLogger(NotificationService.class);

    public void sendRechargeNotification(RechargeCompletedEvent event) {
        // In production this would send SMS/Email
        // For now we log it clearly
        log.info("=================================================");
        log.info("RECHARGE NOTIFICATION SENT");
        log.info("To User ID  : {}", event.getUserId());
        log.info("Phone       : {}", event.getPhoneNumber());
        log.info("Amount      : Rs. {}", event.getAmount());
        log.info("Status      : {}", event.getStatus());
        log.info("Recharge ID : {}", event.getRechargeId());
        
    }

    public void sendPaymentNotification(PaymentCompletedEvent event) {
        log.info("----------------##########----------------------");
        log.info("PAYMENT NOTIFICATION SENT");
        log.info("To User ID      : {}", event.getUserId());
        log.info("Transaction Ref : {}", event.getTransactionRef());
        log.info("Amount          : Rs. {}", event.getAmount());
        log.info("Payment Method  : {}", event.getPaymentMethod());
        log.info("Status          : {}", event.getStatus());
    }
}