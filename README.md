# spring-data-mongodb-hybrid-search

This project is part of the “Beyond Keywords” article series, 
where we explore how to go beyond simple keyword search and build smarter applications with MongoDB and Spring.

It started with semantic search using vector search and Voyage AI embeddings, then evolved to include pre-filters for more precise results, caching strategies to save on embedding generation, and finally Hybrid Search — combining Atlas Search (full-text) with vector search through $rankFusion.

The project demonstrates:

- Vector Search with pre-filters (e.g., genres, year, IMDb rating).
- Atlas Search with compound queries, filters, and should clauses.
- Caching strategies to avoid unnecessary embedding calls.
- Hybrid Search that merges vector similarity and keyword matching.

It also includes a minimal Bootstrap-based UI served by Spring (/static/index.html) and a REST endpoint.

<img src="docs/img/webApp.png" alt="The Movie Search web application" />

## Built With

- Java 21
- Spring Boot Starter Data MongoDB 3.5.4
- [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register) 
- [Voyage AI Embeddings API](https://www.voyageai.com/)

## Getting Started
### 1. Clone the repository

```bash
git clone https://github.com/mongodb-developer/spring-data-mongodb-vector-search.git
```

### 2. Set the environments variables
```
cd spring-data-mongodb-vector-search
export MONGODB_URI="<YOUR_CONNECTION_STRING>" VOYAGE_API_KEY="<API_KEY>"
```

### 3. Run the application
```
mvn spring-boot:run
```

#### 3.1 Use the API (endpoint):

```

POST http://localhost:8080/movies/search
Content-Type: application/json

{
  "query": "a ship that sinks at night after hitting an iceberg",
  "minIMDbRating": 5,
  "yearFrom": 1980,
  "yearTo": 2003,
  "genres": [
    "Drama", "Action"
  ],
  "excludeGenres": false
}

```

#### Or Open the app
```
http://localhost:8080/
```
Use the search bar to find movies (renders title, year, and full plot).

<img src="docs/img/webAppDetails.png" alt="The Movie Search web application"/> 






 
 