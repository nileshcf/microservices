package com.example.user_service.service;

import com.example.user_service.entities.User;
import com.example.user_service.repositories.UserRepository;
import com.example.common.security.JwtTokenProvider;
import com.example.user_service.dto.SignupRequest;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
public class AuthService {

    private UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    public String login(String username, String password) {
        // Dummy credential check
        if ("admin".equals(username) && "password".equals(password)) {
            return jwtTokenProvider.generateToken(username, new HashMap<>());
        }
        throw new RuntimeException("Invalid Credentials");
    }

    public String signup(SignupRequest signupRequest)
    {

        User user = new User();
        user.setUsername(signupRequest.username());
        user.setPassword(signupRequest.password());
        user.setEmail(signupRequest.email());
        user.setName(signupRequest.name());


        userRepository.save(user);

        return jwtTokenProvider.generateToken(user.getUsername(), new HashMap<>());
    }
}