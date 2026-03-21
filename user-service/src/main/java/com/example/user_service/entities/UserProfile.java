package com.example.user_service.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")               // ✅ explicit table name
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(nullable = false)
	private String name;

	@Column
	private Long phoneNumber;                 // ✅ not mandatory at registration

	@Column
	private String avatarUrl;

	@OneToOne                                 // ✅ OneToOne not ManyToOne
	@JoinColumn(name = "user_id", unique = true)
	private User user;
}