package com.example.user_service.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	private String name; // e.g., "Rohan"

	private String avatarUrl; // e.g., "images/avatars/ironman.png"

	private boolean isKid; // If true, filter out 'A' rated content in Catalog Service

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}