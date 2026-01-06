package com.caliq.FoodSecretApiConnection.service;
import com.caliq.core.response.FoodResponse;
import com.caliq.FoodSecretApiConnection.models.FoodSearchResponse;
import org.springframework.stereotype.Service;
@Service
public interface FoodSecretApiService {
    FoodSearchResponse getFoodSearchResponse(String search_expression, int pageNo);
    FoodResponse getFoodResponse(String foodId);
}