package com.example.user_service.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users") // Good practice to pluralize table names
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // UUID is safer for public IDs
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt Hash

    private String phoneNumber; // Critical for Indian market (Hotstar uses this)

    // A User has many Profiles (e.g., "Dad", "Mom", "Kids")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserProfile> profiles;
}