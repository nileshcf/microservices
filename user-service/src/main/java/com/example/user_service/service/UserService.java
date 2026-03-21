//package com.example.user_service.service;
//
//import com.example.user_service.dto.UserProfileResponse;
//import com.example.user_service.entities.User;
//import com.example.user_service.repositories.UserRepository;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RequestHeader;
//
//import java.util.Optional;
//
//@Service
//public class UserService {
//
//	private UserRepository userRepository;
//
//	public UserProfileResponse getMyProfile(String email) {
//
//		Optional<User> user = userRepository.findByEmail(email);
//		if(user.isPresent()) {
//			User userProfile = user.get();
//
//			return new UserProfileResponse(
//					userProfile.getId(),
//					userProfile.getEmail(),
//					userProfile
//			)
//
//		}else {
//
//		}
//
//
//
//
//
//
//
//	}
//
//
//
//}
