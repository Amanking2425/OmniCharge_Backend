package com.omincharge.operator.dto;

import java.time.LocalDateTime;

public class OperatorResponse {
	private Long id;
	private String name;
	private String code;
	private String logoUrl;
	private String status;
	private LocalDateTime createdAt;

	public OperatorResponse() {
		// Default constructor required for serialization/deserialization
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public String getStatus() {
		return status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private OperatorResponse r = new OperatorResponse();

		public Builder id(Long id) {
			r.id = id;
			return this;
		}

		public Builder name(String name) {
			r.name = name;
			return this;
		}

		public Builder code(String code) {
			r.code = code;
			return this;
		}

		public Builder logoUrl(String logoUrl) {
			r.logoUrl = logoUrl;
			return this;
		}

		public Builder status(String status) {
			r.status = status;
			return this;
		}

		public Builder createdAt(LocalDateTime createdAt) {
			r.createdAt = createdAt;
			return this;
		}

		public OperatorResponse build() {
			return r;
		}
	}
}