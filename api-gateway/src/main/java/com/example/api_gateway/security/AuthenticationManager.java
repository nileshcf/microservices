package com.example.api_gateway.security;

import com.example.common.security.JwtValidator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtValidator jwtValidator;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        try {
            Claims claims = jwtValidator.validateAccessToken(authToken);

            String username = claims.getSubject();

            // ✅ Extract real roles from token
            List<String> roles = (List<String>) claims.get("roles");

            // ✅ Convert role strings to Spring Security authorities
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return Mono.just(
                    new UsernamePasswordAuthenticationToken(username, null, authorities)
            );

        } catch (Exception e) {
            return Mono.empty();   // invalid token → unauthenticated
        }
    }
}