package com.caliq.calorie_service.models.response;
import com.caliq.calorie_service.models.types.MealType;
import com.caliq.core.dto.MealDto;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
public record MealByDateResponse(
        String date,
        MealType mealType,
        JsonNode mealDto){}