package com.omincharge.notification.event;

import java.math.BigDecimal;

public class RechargeCompletedEvent {

    private Long rechargeId;
    private Long userId;
    private String phoneNumber;
    private BigDecimal amount;
    private String operatorName;
    private String status;

    public RechargeCompletedEvent() {
    	// Default constructor required for serialization/deserialization
    }

    public Long getRechargeId() { return rechargeId; }
    public Long getUserId() { return userId; }
    public String getPhoneNumber() { return phoneNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getOperatorName() { return operatorName; }
    public String getStatus() { return status; }

    public void setRechargeId(Long rechargeId) { this.rechargeId = rechargeId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public void setStatus(String status) { this.status = status; }
}