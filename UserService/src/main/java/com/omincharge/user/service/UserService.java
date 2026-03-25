package com.omincharge.user.service;

import com.omincharge.user.dto.*;
import com.omincharge.user.entity.User;
import com.omincharge.user.exception.InvalidCredentialsException;
import com.omincharge.user.exception.UserAlreadyExistsException;
import com.omincharge.user.exception.UserNotFoundException;
import com.omincharge.user.repository.UserRepository;
import com.omincharge.user.security.JwtUtil;

import com.omincharge.user.entity.Otp;
import com.omincharge.user.repository.OtpRepository;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       OtpRepository otpRepository,
                       EmailService emailService) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
        this.otpRepository   = otpRepository;
        this.emailService    = emailService;
    }

    public AuthResponse register(RegisterRequest request) {

        logger.info("Registering user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            logger.error("Registration failed - user already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException(
                "User already exists with email: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(User.Role.USER)
                .build();

        userRepository.save(user);

        logger.info("User registered successfully: {}", user.getEmail());

        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(accessToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        logger.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("Login failed - user not found: {}", request.getEmail());
                    return new InvalidCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.error("Login failed - invalid password for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(), user.getRole().name());

        logger.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(accessToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {

        logger.info("Refreshing token");

        if (!jwtUtil.isTokenValid(refreshToken)) {
            logger.error("Invalid refresh token");
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);

        logger.info("Token refresh requested for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found during token refresh: {}", email);
                    return new UserNotFoundException("User not found");
                });

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getEmail(), user.getRole().name());

        logger.info("Token refreshed successfully for email: {}", email);

        return AuthResponse.builder()
                .token(newAccessToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .message("Token refreshed successfully")
                .build();
    }

    public UserProfileResponse getProfile(String token) {

        logger.info("Fetching user profile");

        String cleanToken = token.replace("Bearer ", "");

        if (!jwtUtil.isTokenValid(cleanToken)) {
            logger.error("Invalid token while fetching profile");
            throw new InvalidCredentialsException("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(cleanToken);

        logger.info("Fetching profile for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found while fetching profile: {}", email);
                    return new UserNotFoundException("User not found");
                });

        logger.info("Profile fetched successfully for email: {}", email);

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public List<UserProfileResponse> getAllUsers(String token) {

        logger.info("Fetching all users");

        String cleanToken = token.replace("Bearer ", "");

        if (!jwtUtil.isTokenValid(cleanToken)) {
            logger.error("Invalid token while fetching all users");
            throw new InvalidCredentialsException("Invalid token");
        }

        String role = jwtUtil.extractRole(cleanToken);

        if (!"ADMIN".equals(role)) {
            logger.error("Access denied - non-admin tried to fetch users");
            throw new InvalidCredentialsException("Access denied");
        }

        logger.info("Admin access granted - fetching users");

        return userRepository.findAll()
                .stream()
                .map(user -> UserProfileResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .createdAt(user.getCreatedAt())
                        .build())
                .toList();
    }

    public Map<String, String> sendOtp(SendOtpRequest request) {

        logger.info("Sending OTP to email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            logger.error("OTP request failed - user already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException(
                "User already exists with email: " + request.getEmail());
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        otpRepository.deleteByEmail(request.getEmail());

        Otp otpEntity = Otp.builder()
                .email(request.getEmail())
                .code(otp)
                .build();

        otpRepository.save(otpEntity);

        emailService.sendOtpEmail(request.getEmail(), otp);

        logger.info("OTP sent successfully to email: {}", request.getEmail());

        return Map.of("message", "OTP sent to " + request.getEmail());
    }

    public Map<String, String> verifyOtp(String email, String code) {

        logger.info("Verifying OTP for email: {}", email);

        Otp otp = otpRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> {
                    logger.error("OTP not found for email: {}", email);
                    return new RuntimeException("OTP not found. Please request a new one.");
                });

        if (otp.isUsed()) {
            logger.error("OTP already used for email: {}", email);
            throw new RuntimeException("OTP already used. Please request a new one.");
        }

        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            logger.error("OTP expired for email: {}", email);
            throw new RuntimeException("OTP expired. Please request a new one.");
        }

        if (!otp.getCode().equals(code)) {
            logger.error("Invalid OTP entered for email: {}", email);
            throw new RuntimeException("Invalid OTP. Please try again.");
        }

        otp.setUsed(true);
        otpRepository.save(otp);

        logger.info("OTP verified successfully for email: {}", email);

        return Map.of("message", "OTP verified successfully");
    }
}