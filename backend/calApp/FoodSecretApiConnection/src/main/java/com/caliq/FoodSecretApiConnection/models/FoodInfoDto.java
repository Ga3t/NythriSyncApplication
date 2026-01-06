package com.caliq.FoodSecretApiConnection.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodInfoDto(
        @JsonProperty("food") Food food
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Food(
            @JsonProperty("food_id") String foodId,
            @JsonProperty("food_name") String name,
            @JsonProperty("servings") Servings servings
    ) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Servings(
            @JsonProperty("serving") List<Serving> servingList
    ) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Serving(
            @JsonProperty("serving_id") String servingId,
            @JsonProperty("serving_description") String description,
            @JsonProperty("measurement_description") String measurementDescription,
            @JsonProperty("metric_serving_amount") String metricServingAmount,
            @JsonProperty("calories") String calories,
            @JsonProperty("protein") String protein,
            @JsonProperty("carbohydrate") String carbohydrate,
            @JsonProperty("fat") String fat,
            @JsonProperty("sugar") String sugar,
            @JsonProperty("fiber") String fiber,
            @JsonProperty("cholesterol") String cholesterol
    ) {}
}