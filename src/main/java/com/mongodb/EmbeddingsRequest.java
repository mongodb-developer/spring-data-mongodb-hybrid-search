package com.mongodb;

import java.util.List;

public record EmbeddingsRequest(
    List<String> input,
    String model,
    String input_type,
    Integer output_dimension
) {}