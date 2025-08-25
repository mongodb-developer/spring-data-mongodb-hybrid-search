package com.mongodb;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Vector;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.VectorSearchOperation;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@EnableConfigurationProperties(VoyageConfigProperties.class)
public class MovieService {

	private static final int TOP_K = 10;
	private static final int NUM_CANDIDATES = 150;

	private final MongoTemplate mongoTemplate;
	private final VoyageEmbeddingsClient client;
	private final VoyageConfigProperties config;

 	MovieService(MongoTemplate mongoTemplate, VoyageEmbeddingsClient client, VoyageConfigProperties config) {
		this.mongoTemplate = mongoTemplate;
		this.client = client;
		this.config = config;
	}

	public List<Movie> searchMovies(String query) {
		List<Double> embedding = embedQuery(query);
		Vector vector = Vector.of(embedding);

		VectorSearchOperation search = VectorSearchOperation.search(config.getVectorIndexName())
				.path(config.getVectorField())
				.vector(vector)
				.limit(TOP_K)
				.numCandidates(NUM_CANDIDATES)
				.withSearchScore("score");

		return mongoTemplate.aggregate(newAggregation(Movie.class, search), Movie.class)
				.getMappedResults();
	}

	private List<Double> embedQuery(
			String text) {
		var res = client.embed(new EmbeddingsRequest(
				List.of(text), config.getModel(), "query", config.getOutputDimension()));
		return res.data().getFirst().embedding();
	}
}
