package com.example.user_service.dto;

public record AuthResponse(
		String token,
		String message,
		String userId  // Useful for the frontend to know who logged in
) {
	// Compact Constructor (Optional): Use this if you want a shortcut for success messages
	public AuthResponse(String token, String message) {
		this(token, message, null);
	}
}