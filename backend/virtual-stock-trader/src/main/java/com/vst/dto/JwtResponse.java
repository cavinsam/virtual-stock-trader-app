package com.vst.dto;

import com.vst.entity.Role;
import java.util.Objects;

public class JwtResponse {
    private String token;
    private String email;
    private Role role;

    // --- CONSTRUCTORS ---
    public JwtResponse() {}

    public JwtResponse(String token, String email, Role role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }

    // --- BUILDER PATTERN (MANUAL IMPLEMENTATION) ---
    public static JwtResponseBuilder builder() {
        return new JwtResponseBuilder();
    }

    public static class JwtResponseBuilder {
        private String token;
        private String email;
        private Role role;

        public JwtResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public JwtResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public JwtResponseBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public JwtResponse build() {
            return new JwtResponse(this.token, this.email, this.role);
        }
    }
    
    // --- GETTERS AND SETTERS ---
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    // --- EQUALS, HASHCODE, TOSTRING ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtResponse that = (JwtResponse) o;
        return Objects.equals(token, that.token) && Objects.equals(email, that.email) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, email, role);
    }
}

