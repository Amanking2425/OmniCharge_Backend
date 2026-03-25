package com.omincharge.notification.event;

import java.math.BigDecimal;

public class PaymentCompletedEvent {

    private Long transactionId;
    private Long rechargeId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionRef;

    public PaymentCompletedEvent() {
    	// Default constructor required for serialization/deserialization
    }

    public Long getTransactionId() { return transactionId; }
    public Long getRechargeId() { return rechargeId; }
    public Long getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public String getTransactionRef() { return transactionRef; }

    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public void setRechargeId(Long rechargeId) { this.rechargeId = rechargeId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setStatus(String status) { this.status = status; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
}