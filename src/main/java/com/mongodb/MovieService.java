package com.mongodb;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Vector;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.VectorSearchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@EnableConfigurationProperties(VoyageConfigProperties.class)
public class MovieService {

	private static final int TOP_K = 10;
	private static final int NUM_CANDIDATES = 40;

	private final MongoTemplate mongoTemplate;
	private final VoyageEmbeddingsClient client;
	private final VoyageConfigProperties config;

 	MovieService(MongoTemplate mongoTemplate, VoyageEmbeddingsClient client, VoyageConfigProperties config) {
		this.mongoTemplate = mongoTemplate;
		this.client = client;
		this.config = config;
	}

	public List<Movie> searchMovies(MovieSearchRequest req) {
		List<Double> embedding = embedQuery(req.query());
		Vector vector = Vector.of(embedding);

		VectorSearchOperation search = VectorSearchOperation.search(config.vectorIndexName())
				.path(config.vectorField())
				.vector(vector)
				.limit(TOP_K)
				.filter(buildCriteria(req))
				.numCandidates(NUM_CANDIDATES)
				.withSearchScore("score");

		return mongoTemplate.aggregate(newAggregation(Movie.class, search), Movie.class)
				.getMappedResults();
	}

	private List<Double> embedQuery(
			String text) {
		var res = client.embed(new EmbeddingsRequest(
				List.of(text), config.model(), "query", config.outputDimension()));
		return res.data().getFirst().embedding();
	}

	private Criteria buildCriteria(final MovieSearchRequest req) {
		final List<Criteria> parts = new ArrayList<>(3);

		final List<String> genres = req.genres();

		if (genres != null) {
			final List<String> cleaned = genres.stream()
					.filter(Objects::nonNull)
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.distinct()
					.toList();

			if (!cleaned.isEmpty()) {
				parts.add(req.excludeGenres()
						? Criteria.where("genres").nin(cleaned)
						: Criteria.where("genres").in(cleaned));
			}
		}

		Integer from = req.yearFrom();
		Integer to   = req.yearTo();
		if (from != null || to != null) {
			if (from != null && to != null && from > to) {
				final int tmp = from; from = to; to = tmp; // swap
			}
			Criteria y = Criteria.where("year");
			if (from != null) y = y.gte(from);
			if (to   != null) y = y.lte(to);
			parts.add(y);
		}

		if (req.minImdbRating() != null) {
			parts.add(Criteria.where("imdb.rating").gte(req.minImdbRating()));
		}

		return switch (parts.size()) {
			case 0 -> new Criteria();
			case 1 -> parts.getFirst();
			default -> new Criteria().andOperator(parts.toArray(new Criteria[0]));
		};
	}


}
