package com.example.api_gateway.filter;

import com.example.api_gateway.config.RouterValidator;
import com.example.common.security.JwtTokenProvider; // From Common Lib
import com.example.common.security.JwtValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	@Autowired
	private RouterValidator routerValidator;

	@Autowired
	private JwtValidator jwtValidator;

	public AuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {

			// 1. Check if the Request needs security
			if (routerValidator.isSecured.test(exchange.getRequest())) {

				// 2. Check for Auth Header
				if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
				}

				String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
				if (authHeader != null && authHeader.startsWith("Bearer ")) {
					authHeader = authHeader.substring(7); // Remove "Bearer "
				}

				// 3. Validate Token
				try {
					// You might need to add a simple 'validateToken(String)' method to your JwtTokenProvider in Common
					// if it currently expects an Authentication object.
					// For now, assuming you have a boolean validation method:
					jwtValidator.validateToken(authHeader);

					// 4. (Optional) Extract User ID and pass it downstream
					// String userId = jwtTokenProvider.getUserIdFromToken(authHeader);
					// ServerHttpRequest request = exchange.getRequest()
					//        .mutate()
					//        .header("X-User-Id", userId)
					//        .build();
					// return chain.filter(exchange.mutate().request(request).build());

				} catch (Exception e) {
					return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
				}
			}
			return chain.filter(exchange);
		});
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		exchange.getResponse().setStatusCode(httpStatus);
		return exchange.getResponse().setComplete();
	}

	public static class Config {
		// configuration properties if needed
	}
}