package com.mongodb;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.search.FuzzySearchOptions;
import com.mongodb.client.model.search.SearchOperator;
import com.mongodb.client.model.search.SearchOptions;
import com.mongodb.client.model.search.SearchPath;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.VectorSearchOperation;
import org.springframework.stereotype.Service;

import java.util.List;

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

	private BsonDocument createSearch(String query) {
		return Aggregates.search(
				SearchOperator.text(
						SearchPath.fieldPath("title"),
						query
				).fuzzy(FuzzySearchOptions.fuzzySearchOptions().maxEdits(2)),
				SearchOptions.searchOptions().index("fulltextsearch")
		).toBsonDocument();
	}

	private Bson createVectorSearch(MovieSearchRequest req) {
		return VectorSearchOperation.search(config.vectorIndexName())
				.path(config.vectorField())
				.vector(embeddingService.embedQuery(req.query()))
				.limit(config.topK())
				.filter(req.toCriteria())
				.numCandidates(config.numCandidates())
				.withSearchScore("score").toDocument(Aggregation.DEFAULT_CONTEXT);
	}

	public List<Movie> searchMovies(MovieSearchRequest req) {
		AggregationOperation rankFusion = context -> new Document("$rankFusion",
				new Document("input",
						new Document("pipelines",
								new Document("searchPipeline", List.of(createSearch(req.query())))
										.append("vectorPipeline", List.of(createVectorSearch(req)))))
						.append("combination",
								new Document("weights",
										new Document("searchPipeline", 0.1d)
												.append("vectorPipeline", 0.9d)))
						.append("scoreDetails", false));

		Aggregation aggregation = Aggregation.newAggregation(rankFusion);

		return mongoTemplate.aggregate(aggregation, "embedded_movies", Movie.class).getMappedResults();
	}
}
