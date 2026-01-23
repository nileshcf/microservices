package com.example.user_service.service;

import com.example.common.security.JwtTokenProvider; // Import from Common
import com.example.user_service.dto.AuthResponse;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.SignupRequest;
import com.example.user_service.entities.User;
import com.example.user_service.entities.UserProfile;
import com.example.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already taken");
        }

        // 1. Create User
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhoneNumber(request.phoneNumber());

        // 2. Create Default Profile
        UserProfile defaultProfile = UserProfile.builder()
                .name("My Profile")
                .isKid(false)
                .user(user)
                .build();
        user.setProfiles(List.of(defaultProfile));

        userRepository.save(user);

        // 3. Generate Token
        // Add extra data to token payload if needed (e.g., roles)
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", "ROLE_USER");

        String token = jwtTokenProvider.generateToken(user.getEmail(), claims);

        return new AuthResponse(token, "User registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // 2. Prepare Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", "ROLE_USER"); // You can fetch real roles from DB here

        // 3. Generate Token
        String token = jwtTokenProvider.generateToken(request.email(), claims);

        return new AuthResponse(token, "Login Successful");
    }
}