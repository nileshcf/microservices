package com.example.catalog_service.entities;

import lombok.Data;
import java.util.List;

@Data
public class Season {
 private int seasonNumber;
 private List<Episode> episodes;
}

@Data
class Episode {
 private int episodeNumber;
 private String title;
 private String videoUrl;
}