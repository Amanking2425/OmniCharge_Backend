package com.omincharge.user.dto;

public class AuthResponse {
	private String token;
	private String email;
	private String role;
	private String message;
	private Long userId;
	private String firstName;
	private String lastName;

	public AuthResponse() {
		// Default constructor required for serialization/deserialization
	}

	public String getToken() {
		return token;
	}

	public String getEmail() {
		return email;
	}

	public String getRole() {
		return role;
	}

	public String getMessage() {
		return message;
	}

	public Long getUserId() {
		return userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private AuthResponse r = new AuthResponse();

		public Builder token(String token) {
			r.token = token;
			return this;
		}

		public Builder email(String email) {
			r.email = email;
			return this;
		}

		public Builder role(String role) {
			r.role = role;
			return this;
		}

		public Builder message(String message) {
			r.message = message;
			return this;
		}

		public Builder userId(Long userId) {
			r.userId = userId;
			return this;
		}

		public Builder firstName(String firstName) {
			r.firstName = firstName;
			return this;
		}

		public Builder lastName(String lastName) {
			r.lastName = lastName;
			return this;
		}

		public AuthResponse build() {
			return r;
		}
	}
}