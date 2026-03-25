package com.omincharge.recharge.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RechargeResponse {
	private Long id;
	private Long userId;
	private Long operatorId;
	private Long planId;
	private String phoneNumber;
	private BigDecimal amount;
	private String status;
	private String failureReason;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public RechargeResponse() {
		// Default constructor required for serialization/deserialization
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public Long getPlanId() {
		return planId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getStatus() {
		return status;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private RechargeResponse r = new RechargeResponse();

		public Builder id(Long id) {
			r.id = id;
			return this;
		}

		public Builder userId(Long userId) {
			r.userId = userId;
			return this;
		}

		public Builder operatorId(Long operatorId) {
			r.operatorId = operatorId;
			return this;
		}

		public Builder planId(Long planId) {
			r.planId = planId;
			return this;
		}

		public Builder phoneNumber(String phoneNumber) {
			r.phoneNumber = phoneNumber;
			return this;
		}

		public Builder amount(BigDecimal amount) {
			r.amount = amount;
			return this;
		}

		public Builder status(String status) {
			r.status = status;
			return this;
		}

		public Builder failureReason(String failureReason) {
			r.failureReason = failureReason;
			return this;
		}

		public Builder createdAt(LocalDateTime createdAt) {
			r.createdAt = createdAt;
			return this;
		}

		public Builder updatedAt(LocalDateTime updatedAt) {
			r.updatedAt = updatedAt;
			return this;
		}

		public RechargeResponse build() {
			return r;
		}
	}
}