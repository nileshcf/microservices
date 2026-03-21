package com.example.user_service.dto;

import java.time.Instant;
import java.util.List;

public record UserProfileResponse(
		String userId,
		String email,
		String username,
		List<String> roles,
		boolean emailVerified,
		Instant createdAt,
		Instant updatedAt
) {}