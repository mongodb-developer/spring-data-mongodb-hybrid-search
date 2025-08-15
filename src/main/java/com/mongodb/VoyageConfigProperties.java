package com.mongodb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "voyage")
public class VoyageConfigProperties {

    private String model;
    private int outputDimension;
    private String vectorIndexName;
    private String vectorField;
    private int topK;
    private int numCandidates;

    // getters e setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getOutputDimension() { return outputDimension; }
    public void setOutputDimension(int outputDimension) { this.outputDimension = outputDimension; }

    public String getVectorIndexName() { return vectorIndexName; }
    public void setVectorIndexName(String vectorIndexName) { this.vectorIndexName = vectorIndexName; }

    public String getVectorField() { return vectorField; }
    public void setVectorField(String vectorField) { this.vectorField = vectorField; }

    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }

    public int getNumCandidates() { return numCandidates; }
    public void setNumCandidates(int numCandidates) { this.numCandidates = numCandidates; }
}
