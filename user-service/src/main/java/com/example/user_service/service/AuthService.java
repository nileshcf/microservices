package com.example.user_service.service;

import com.example.common.security.JwtConstants;
import com.example.common.security.JwtTokenProvider; // Import from Common
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.RegisterResponse;
import com.example.user_service.dto.SignupRequest;
import com.example.user_service.entities.User;
import com.example.user_service.entities.UserProfile;
import com.example.user_service.enums.Roles;
import com.example.user_service.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public RegisterResponse register(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already taken");
        }

        // 1. Create User
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setEmailVerified(false);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(List.of(Roles.ROLE_USER));
        user.setCreatedAt(LocalDateTime.now());


        UserProfile profile = UserProfile.builder()
                .user(user)
                .phoneNumber(request.phoneNumber())
                .build();

        user.setProfile(profile);
        userRepository.save(user);

        log.info("User {} registered successfully", user.getEmail());

        return new RegisterResponse("User registered successfully", user.getEmail(), user.getId(), user.getCreatedAt());
    }

    public LoginResponse login(LoginRequest request) {

        // 1. Authenticate first — throws exception if credentials wrong
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());  // ✅ see exact error
            throw e;
        }

        // 2. Fetch user — guaranteed to exist after authentication passes
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Convert Enum roles to Strings for JWT
        List<String> roleStrings = user.getRoles().stream()
                .map(Roles::name)                         // ROLE_USER enum → "ROLE_USER" String
                .collect(Collectors.toList());

        // 4. Prepare Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", roleStrings);                 // ✅ Strings in JWT, not Enum
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());

        // 5. Generate tokens
        String accessToken  = jwtTokenProvider.generateAccessToken(
                request.email(), claims, JwtConstants.ACCESS_TOKEN_EXPIRY);
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                request.email(), JwtConstants.REFRESH_TOKEN_EXPIRY);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                JwtConstants.ACCESS_TOKEN_EXPIRY / 1000,  // ✅ convert ms to seconds for frontend
                user.getId(),
                user.getEmail(),
                roleStrings                               // ✅ return Strings not Enum to frontend
        );
    }
}