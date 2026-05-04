package com.omincharge.user.controller;

import com.omincharge.user.dto.*;
import com.omincharge.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "User registration, login and profile endpoints")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        logger.info("Register request received for email: {}", request.getEmail());

        return ResponseEntity.ok(userService.register(request));
    }

    @Operation(summary = "Login and get JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        logger.info("Login request received for email: {}", request.getEmail());

        return ResponseEntity.ok(userService.login(request));
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody Map<String, String> request) {

        logger.info("Token refresh request received");

        return ResponseEntity.ok(
            userService.refreshToken(request.get("refreshToken")));
    }

    @Operation(summary = "Get user profile (requires Bearer token)")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @RequestHeader("Authorization") String token) {

        logger.info("Profile request received");

        return ResponseEntity.ok(userService.getProfile(token));
    }

    @Operation(summary = "Get all users — admin only")
    @GetMapping("/all")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers(
            @RequestHeader("Authorization") String token) {

        logger.info("Get all users request received");

        return ResponseEntity.ok(userService.getAllUsers(token));
    }
    



    @Operation(summary = "Send OTP to email for registration")
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {

        logger.info("Send OTP request for email: {}", request.getEmail());

        return ResponseEntity.ok(userService.sendOtp(request));
    }

    @Operation(summary = "Verify OTP before registration")
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        logger.info("Verify OTP request for email: {}", request.getEmail());

        return ResponseEntity.ok(
            userService.verifyOtp(request.getEmail(), request.getOtp()));
    }
    
}

