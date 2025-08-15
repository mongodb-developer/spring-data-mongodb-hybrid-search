package com.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("embedded_movies")
public record Movie(
	String id,
	String title,
	String year,
	String fullplot)
{}
