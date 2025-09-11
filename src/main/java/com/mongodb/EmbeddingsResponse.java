package com.mongodb;

import java.util.List;

public record EmbeddingsResponse(List<Item> data) {
  public record Item(List<Double> embedding) {}
}
