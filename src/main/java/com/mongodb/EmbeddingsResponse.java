package com.mongodb;

import java.util.List;

public record EmbeddingsResponse(List<Item> data) {
  record Item(List<Double> embedding) {}
}