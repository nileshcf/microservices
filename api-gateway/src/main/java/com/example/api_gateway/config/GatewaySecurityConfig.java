package com.example.api_gateway.config;

import com.example.api_gateway.security.AuthenticationManager;
import com.example.api_gateway.security.SecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;


@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class GatewaySecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // ✅ disable default basic auth
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // ✅ disable default form login
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/user/auth/**").permitAll()     // ✅ all auth endpoints public
                        .pathMatchers("/actuator/health").permitAll()  // ✅ health check public
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((swe, e) -> {
                            // ✅ 401 — not logged in
                            swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return swe.getResponse().setComplete();
                        })
                        .accessDeniedHandler((swe, e) -> {
                            // ✅ 403 — logged in but wrong role
                            swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return swe.getResponse().setComplete();
                        })
                )
                .build();
    }
}