package com.mongodb;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "voyage")
public record VoyageConfigProperties(
     String model,
     int outputDimension,
     String vectorIndexName,
     String vectorField,
     int topK,
     int numCandidates,
     String baseUrl,
     String apiKey)
{}
