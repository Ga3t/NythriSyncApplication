package com.caliq.api_conection_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
public record AddFoodDto(
        @JsonProperty("barcode") String barcode,
        @JsonProperty("brand_name") String brand_name,
        @JsonProperty("name") String name,
        @JsonProperty("kcal") BigDecimal kcal,
        @JsonProperty("proteins") BigDecimal proteins,
        @JsonProperty("fat") BigDecimal fat,
        @JsonProperty("carbs") BigDecimal carbs,
        @JsonProperty("fiber") BigDecimal fiber,
        @JsonProperty("sugar") BigDecimal sugar,
        @JsonProperty("cholesterol") BigDecimal cholesterol,
        @JsonProperty("grade") String grade
        ){
}