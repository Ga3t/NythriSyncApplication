package com.caliq.FoodSecretApiConnection.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodSearchDto(
        @JsonProperty("foods") Foods foods
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Foods(
            @JsonProperty("food") List<Food> food,
            @JsonProperty("max_results") String maxResults,
            @JsonProperty("page_number") String pageNumber,
            @JsonProperty("total_results") String totalResults
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Food(
            @JsonProperty("brand_name") String brandName,
            @JsonProperty("food_description") String foodDescription,
            @JsonProperty("food_id") String foodId,
            @JsonProperty("food_name") String foodName,
            @JsonProperty("food_type") String foodType,
            @JsonProperty("food_url") String foodUrl
    ) {}
}