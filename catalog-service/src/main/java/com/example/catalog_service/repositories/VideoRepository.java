package com.example.catalog_service.repositories;

import com.example.catalog_service.entities.VideoItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends MongoRepository<VideoItem, String> {

	// Find by Type (e.g., Get all Movies)
	List<VideoItem> findByType(String type);

	// Simple Search (Matches title containing text, case insensitive)
	List<VideoItem> findByTitleContainingIgnoreCase(String title);

	// Native Mongo Query: Find all "Action" movies
	@Query("{ 'genre' : ?0, 'type' : 'MOVIE' }")
	List<VideoItem> findMoviesByGenre(String genre);
}