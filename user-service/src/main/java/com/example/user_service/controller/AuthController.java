package com.example.user_service.controller;

import com.example.user_service.dto.LoginResponse;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.RegisterResponse;
import com.example.user_service.dto.SignupRequest;
import com.example.user_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth") // <--- CORRECT! (Gateway converts /user/auth -> /auth)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Endpoint: POST /user/auth/register (via Gateway)
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // Endpoint: POST /user/auth/login (via Gateway)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}