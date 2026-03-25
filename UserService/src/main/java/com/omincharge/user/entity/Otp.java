package com.omincharge.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
public class Otp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String code;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	private boolean used;

	@PrePersist
	public void prePersist() {
		this.expiresAt = LocalDateTime.now().plusMinutes(5);
		this.used = false;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getCode() {
		return code;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public boolean isUsed() {
		return used;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Otp o = new Otp();

		public Builder email(String email) {
			o.email = email;
			return this;
		}

		public Builder code(String code) {
			o.code = code;
			return this;
		}

		public Otp build() {
			return o;
		}
	}
}