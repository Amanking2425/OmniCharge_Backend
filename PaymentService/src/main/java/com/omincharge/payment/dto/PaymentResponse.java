package com.omincharge.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
	private Long id;
	private Long rechargeId;
	private Long userId;
	private BigDecimal amount;
	private String paymentMethod;
	private String status;
	private String transactionRef;
	private String failureReason;
	private LocalDateTime createdAt;

	public PaymentResponse() {
		// Default constructor required for serialization/deserialization
	}

	public Long getId() {
		return id;
	}

	public Long getRechargeId() {
		return rechargeId;
	}

	public Long getUserId() {
		return userId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public String getStatus() {
		return status;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setRechargeId(Long rechargeId) {
		this.rechargeId = rechargeId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private PaymentResponse r = new PaymentResponse();

		public Builder id(Long id) {
			r.id = id;
			return this;
		}

		public Builder rechargeId(Long rechargeId) {
			r.rechargeId = rechargeId;
			return this;
		}

		public Builder userId(Long userId) {
			r.userId = userId;
			return this;
		}

		public Builder amount(BigDecimal amount) {
			r.amount = amount;
			return this;
		}

		public Builder paymentMethod(String paymentMethod) {
			r.paymentMethod = paymentMethod;
			return this;
		}

		public Builder status(String status) {
			r.status = status;
			return this;
		}

		public Builder transactionRef(String transactionRef) {
			r.transactionRef = transactionRef;
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

		public PaymentResponse build() {
			return r;
		}
	}
}