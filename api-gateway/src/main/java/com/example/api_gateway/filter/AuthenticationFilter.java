package com.example.api_gateway.filter;

import com.example.api_gateway.config.RouterValidator;
import com.example.common.security.JwtTokenProvider; // From Common Lib
import com.example.common.security.JwtValidator;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	private final RouterValidator routerValidator;
	private final JwtValidator jwtValidator;

	// ✅ Constructor injection
	public AuthenticationFilter(RouterValidator routerValidator, JwtValidator jwtValidator) {
		super(Config.class);
		this.routerValidator = routerValidator;
		this.jwtValidator = jwtValidator;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {

			// 1. Check if endpoint needs authentication
			if (routerValidator.isSecured.test(exchange.getRequest())) {

				// 2. Check Authorization header exists
				if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
				}

				String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

				// 3. Check Bearer format explicitly
				if (authHeader == null || !authHeader.startsWith("Bearer ")) {
					return onError(exchange, "Invalid Authorization Header format", HttpStatus.UNAUTHORIZED);
				}

				String token = authHeader.substring(7);

				// 4. Validate token
				try {
					Claims claims = jwtValidator.validateAccessToken(token);  // ✅ correct method

					// 5. ✅ Forward user info to downstream services as headers
					// Downstream services read X-User-Id, X-User-Email, X-User-Roles
					// instead of decoding JWT themselves
					String userId = (String) claims.get("userId");
					String email  = claims.getSubject();
					String roles  = claims.get("roles").toString();

					ServerHttpRequest mutatedRequest = exchange.getRequest()
							.mutate()
							.header("X-User-Id", userId)
							.header("X-User-Email", email)
							.header("X-User-Roles", roles)
							.build();

					return chain.filter(exchange.mutate().request(mutatedRequest).build());

				} catch (Exception e) {
					return onError(exchange, "Invalid or Expired Token", HttpStatus.UNAUTHORIZED);
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
		// add config properties here if needed
	}
}