package com.vst.controller;

import com.vst.dto.JwtResponse;
import com.vst.dto.LoginRequest;
import com.vst.dto.SignUpRequest;
import com.vst.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // Allow React app
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // TEST ENDPOINT - Remove this after confirming everything works
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("🎉 Auth endpoints are working! Spring Boot ↔ React connection established!");
    }

    // Get application status - useful for React to check if backend is running
    @GetMapping("/status")
    public ResponseEntity<Object> getStatus() {
        return ResponseEntity.ok(new Object() {
            public final String status = "online";
            public final String message = "Virtual Stock Trader API is running";
            public final long timestamp = System.currentTimeMillis();
        });
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            authService.signup(signUpRequest);
            return ResponseEntity.ok(new Object() {
                public final String message = "User registered successfully!";
                public final boolean success = true;
            });
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Object() {
                public final String message = e.getMessage();
                public final boolean success = false;
            });
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.login(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Object() {
                public final String message = "Invalid credentials";
                public final boolean success = false;
            });
        }
    }

    // Logout endpoint (optional - mainly for clearing client-side tokens)
    @PostMapping("/logout")
    public ResponseEntity<Object> logout() {
        return ResponseEntity.ok(new Object() {
            public final String message = "Logged out successfully";
            public final boolean success = true;
        });
    }

    // Validate token endpoint - useful for React to check if user is still authenticated
    @GetMapping("/validate")
    public ResponseEntity<Object> validateToken() {
        // This will be protected by JWT filter, so if it reaches here, token is valid
        return ResponseEntity.ok(new Object() {
            public final String message = "Token is valid";
            public final boolean authenticated = true;
            public final long timestamp = System.currentTimeMillis();
        });
    }
}