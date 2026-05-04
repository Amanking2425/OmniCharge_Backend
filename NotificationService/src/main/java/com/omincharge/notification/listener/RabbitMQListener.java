package com.omincharge.notification.listener;

import com.omincharge.notification.event.PaymentCompletedEvent;
import com.omincharge.notification.event.RechargeCompletedEvent;
import com.omincharge.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {

    private final NotificationService notificationService;

    public RabbitMQListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ✅ PAYMENT LISTENER
    @RabbitListener(queues = "${rabbitmq.queue.payment-notification}")
    public void handlePayment(PaymentCompletedEvent event) {
        System.out.println("Received PAYMENT event");
        notificationService.sendPaymentNotification(event);
    }

    // ✅ RECHARGE LISTENER
    @RabbitListener(queues = "${rabbitmq.queue.recharge-notification}")
    public void handleRecharge(RechargeCompletedEvent event) {
        System.out.println("Received RECHARGE event");
        notificationService.sendRechargeNotification(event);
    }
}