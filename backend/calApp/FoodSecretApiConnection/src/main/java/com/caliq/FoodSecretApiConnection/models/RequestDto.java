package com.caliq.FoodSecretApiConnection.models;
import com.fasterxml.jackson.annotation.JsonProperty;
public record RequestDto(@JsonProperty("search") String searchRequest) {
}