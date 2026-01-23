package com.example.catalog_service.controller;

import com.example.catalog_service.entities.VideoItem;
import com.example.catalog_service.repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

	private final VideoRepository videoRepository;

	@GetMapping("/all")
	public List<VideoItem> getAllVideos() {
		return videoRepository.findAll();
	}

	@GetMapping("/movies")
	public List<VideoItem> getMovies() {
		return videoRepository.findByType("MOVIE");
	}

	// This is the endpoint the Video Player calls
	@GetMapping("/video/{id}")
	public VideoItem getVideoById(@PathVariable String id) {
		return videoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Video not found"));
	}

	// Admin only: Add content
	@PostMapping("/add")
	public VideoItem addVideo(@RequestBody VideoItem video) {
		return videoRepository.save(video);
	}
}