package com.mongodb;

import java.util.List;

public record MovieSearchRequest(String query,
								 Integer yearFrom, Integer yearTo, List<String> genres, Double minImdbRating, boolean excludeGenres) {}