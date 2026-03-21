package com.omincharge.user.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, String email, String role, String message) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.message = message;
    }

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getMessage() { return message; }

    public void setToken(String token) { this.token = token; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setMessage(String message) { this.message = message; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token, email, role, message;
        public Builder token(String token) { this.token = token; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public AuthResponse build() { return new AuthResponse(token, email, role, message); }
    }
}