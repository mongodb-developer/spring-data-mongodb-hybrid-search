package com.mongodb;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.VectorSearchOperation;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@EnableConfigurationProperties(VoyageConfigProperties.class)
public class MovieService {

	private final MongoTemplate mongoTemplate;
	private final VoyageConfigProperties config;
	private final EmbeddingService embeddingService;

 	MovieService(MongoTemplate mongoTemplate, VoyageConfigProperties config, EmbeddingService embeddingService) {
		this.mongoTemplate = mongoTemplate;
		this.config = config;
		this.embeddingService = embeddingService;
	}

	public List<Movie> searchMovies(MovieSearchRequest req) {
		VectorSearchOperation search = VectorSearchOperation.search(config.vectorIndexName())
				.path(config.vectorField())
				.vector(embeddingService.embedQuery(req.query()))
				.limit(config.topK())
				.filter(req.toCriteria())
				.numCandidates(config.numCandidates())
				.withSearchScore("score");

		return mongoTemplate.aggregate(newAggregation(Movie.class, search), Movie.class)
				.getMappedResults();
	}
}
