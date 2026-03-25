package com.omincharge.operator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PlanRequest {

	@NotBlank(message = "Plan name is required")
	private String name;

	@NotNull(message = "Amount is required")
	private BigDecimal amount;

	@NotNull(message = "Validity days is required")
	private Integer validityDays;

	private String dataAllowance;
	private String description;

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
}