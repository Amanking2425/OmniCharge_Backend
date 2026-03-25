package com.omincharge.user;

import com.omincharge.user.dto.*;
import com.omincharge.user.entity.User;
import com.omincharge.user.exception.UserAlreadyExistsException;
import com.omincharge.user.exception.InvalidCredentialsException;
import com.omincharge.user.repository.UserRepository;
import com.omincharge.user.security.JwtUtil;
import com.omincharge.user.service.UserService;
import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceApplicationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    // ── Register Tests ────────────────────────────

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("password123");
        request.setFirstName("Aman");
        request.setLastName("Kumar");
        request.setPhone("9876543210");

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtUtil.generateAccessToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return User.builder()
                    .email(u.getEmail())
                    .password(u.getPassword())
                    .firstName(u.getFirstName())
                    .lastName(u.getLastName())
                    .phone(u.getPhone())
                    .role(u.getRole())
                    .build();
        });

        AuthResponse response = userService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getEmail()).isEqualTo("test@gmail.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ThrowsException_WhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("existing@gmail.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.register(request));

        verify(userRepository, never()).save(any());
    }

    // ── Login Tests ───────────────────────────────

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("password123");

        User user = User.builder()
                .email("test@gmail.com")
                .password("encodedPassword")
                .role(User.Role.USER)
                .build();

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateAccessToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refresh-token");

        AuthResponse response = userService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void login_ThrowsException_WhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@gmail.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(request));
    }

    @Test
    void login_ThrowsException_WhenPasswordIncorrect() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("wrongpassword");

        User user = User.builder()
                .email("test@gmail.com")
                .password("encodedPassword")
                .role(User.Role.USER)
                .build();

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(request));
    }

    @Test
    void refreshToken_ThrowsException_WhenTokenInvalid() {
        when(jwtUtil.isTokenValid("invalid-token")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.refreshToken("invalid-token"));
    }
}