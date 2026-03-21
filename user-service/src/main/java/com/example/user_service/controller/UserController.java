package com.example.user_service.controller;

import com.example.user_service.dto.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class UserController {


	@GetMapping
	public ResponseEntity<UserProfileResponse> getMyProfile(@RequestHeader("X-User-Email") String email) {


	}

}
