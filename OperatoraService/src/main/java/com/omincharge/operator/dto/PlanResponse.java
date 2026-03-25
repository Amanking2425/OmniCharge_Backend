package com.omincharge.operator.dto;

import java.math.BigDecimal;

public class PlanResponse {
	private Long id;
	private Long operatorId;
	private String operatorName;
	private String name;
	private BigDecimal amount;
	private Integer validityDays;
	private String dataAllowance;
	private String description;
	private String status;

	public PlanResponse() {
		// Default constructor required for serialization/deserialization
	}

	public Long getId() {
		return id;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Integer getValidityDays() {
		return validityDays;
	}

	public String getDataAllowance() {
		return dataAllowance;
	}

	public String getDescription() {
		return description;
	}

	public String getStatus() {
		return status;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setValidityDays(Integer validityDays) {
		this.validityDays = validityDays;
	}

	public void setDataAllowance(String dataAllowance) {
		this.dataAllowance = dataAllowance;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private PlanResponse r = new PlanResponse();

		public Builder id(Long id) {
			r.id = id;
			return this;
		}

		public Builder operatorId(Long operatorId) {
			r.operatorId = operatorId;
			return this;
		}

		public Builder operatorName(String operatorName) {
			r.operatorName = operatorName;
			return this;
		}

		public Builder name(String name) {
			r.name = name;
			return this;
		}

		public Builder amount(BigDecimal amount) {
			r.amount = amount;
			return this;
		}

		public Builder validityDays(Integer validityDays) {
			r.validityDays = validityDays;
			return this;
		}

		public Builder dataAllowance(String dataAllowance) {
			r.dataAllowance = dataAllowance;
			return this;
		}

		public Builder description(String description) {
			r.description = description;
			return this;
		}

		public Builder status(String status) {
			r.status = status;
			return this;
		}

		public PlanResponse build() {
			return r;
		}
	}
}