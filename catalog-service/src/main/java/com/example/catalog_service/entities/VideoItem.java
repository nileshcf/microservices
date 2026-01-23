package com.example.catalog_service.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "videos") // MongoDB Collection Name
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VideoItem {

	@Id
	private String id;

	private String title;
	private String description;

	private String type; // "MOVIE" or "TV_SHOW"

	private String genre; // "Action", "Drama"

	// Images
	private String thumbnailVertical;  // For mobile app lists
	private String thumbnailHorizontal; // For desktop hero banner

	// For Movies
	private String videoUrl; // The M3U8 link from CDN
	private String trailerUrl;
	private Integer durationSeconds;

	// For TV Shows (Optional: You could also make this a separate collection)
	private List<Season> seasons;

	// Access Control
	private boolean isPremium; // true = Requires Subscription
}