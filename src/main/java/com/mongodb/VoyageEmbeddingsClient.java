package com.mongodb;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "voyage",
        url = "${voyage.base-url}",
        configuration = VoyageFeignConfig.class
)
public interface VoyageEmbeddingsClient {
  @PostMapping("/embeddings")
  EmbeddingsResponse embed(@RequestBody EmbeddingsRequest body);
}