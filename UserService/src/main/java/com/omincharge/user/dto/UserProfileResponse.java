package com.omincharge.user.dto;

import java.time.LocalDateTime;

public class UserProfileResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private LocalDateTime createdAt;

    public UserProfileResponse() {}

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { this.role = role; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UserProfileResponse r = new UserProfileResponse();
        public Builder id(Long id) { r.id = id; return this; }
        public Builder email(String email) { r.email = email; return this; }
        public Builder firstName(String firstName) { r.firstName = firstName; return this; }
        public Builder lastName(String lastName) { r.lastName = lastName; return this; }
        public Builder phone(String phone) { r.phone = phone; return this; }
        public Builder role(String role) { r.role = role; return this; }
        public Builder createdAt(LocalDateTime createdAt) { r.createdAt = createdAt; return this; }
        public UserProfileResponse build() { return r; }
    }
}