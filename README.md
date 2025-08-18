# spring-data-mongodb-vector-search
This project demonstrates how to define vector-indexed fields, configure vector search indices, and execute similarity queries using Spring Data MongoDB

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

### 4. Searching movies

```
GET http://localhost:8080/movies?query=Find movies featuring pyramids and space travel
```


 