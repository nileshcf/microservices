package com.example.user_service.service;

import com.example.common.security.JwtTokenProvider;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    public String login(String username, String password) {
        // Dummy credential check
        if ("admin".equals(username) && "password".equals(password)) {
            return jwtTokenProvider.generateToken(username, new HashMap<>());
        }
        throw new RuntimeException("Invalid Credentials");
    }
}