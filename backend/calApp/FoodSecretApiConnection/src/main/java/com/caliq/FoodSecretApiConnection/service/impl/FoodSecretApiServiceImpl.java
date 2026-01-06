package com.caliq.FoodSecretApiConnection.service.impl;
import com.caliq.FoodSecretApiConnection.exceptions.ProductNotFoundException;
import com.caliq.FoodSecretApiConnection.models.*;
import com.caliq.FoodSecretApiConnection.repository.FoodSecretRepository;
import com.caliq.FoodSecretApiConnection.service.FoodSecretApiService;
import com.caliq.core.response.FoodResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;
import static com.caliq.FoodSecretApiConnection.utils.StringBigDecimalParser.parseBigDecimalSafe;
@Service
@Primary
public class FoodSecretApiServiceImpl implements FoodSecretApiService {
    private final RestClient apiRestClient;
    private final FoodSecretRepository foodSecretRepository;
    public FoodSecretApiServiceImpl(RestClient apiRestClient, FoodSecretRepository foodSecretRepository) {
        this.apiRestClient = apiRestClient;
        this.foodSecretRepository = foodSecretRepository;
    }
    @Override
    public FoodSearchResponse getFoodSearchResponse(String search_expression, int pageNo) {
        String uri = "https://platform.fatsecret.com/rest/foods/search/v1"
                +"?search_expression={search_expression}&format=json&region=PL&page_number={pageNo}";
        FoodSearchDto dto = apiRestClient.get()
                .uri(uri,search_expression, pageNo)
                .retrieve()
                .body(FoodSearchDto.class);
        if (dto == null) {
            throw new ProductNotFoundException("Product not found in database please add it manually or try again");
        }
        List<FoodSearchResponse.FoodItem> items = dto.foods().food().stream()
                .filter(f -> f.foodDescription() != null &&
                        f.foodDescription().toLowerCase().contains("100g"))
                .map(f -> new FoodSearchResponse.FoodItem(
                        f.foodId(),
                        f.foodName(),
                        f.foodDescription()
                ))
                .toList();
        return new FoodSearchResponse(items, String.valueOf(items.size()));
    }
    @Override
    @Cacheable(value = "foodSecretCache", key = "#foodId")
    public FoodResponse getFoodResponse(String foodId) {
        FoodModel foodmodel = foodSecretRepository.findByFoodSecretId(foodId).orElse(null);
        FoodResponse response = new FoodResponse();
        if (foodmodel == null) {
            String uri = "https://platform.fatsecret.com/rest/food/v4?food_id={foodId}&format=json";
            FoodInfoDto dto = apiRestClient.get()
                    .uri(uri,foodId)
                    .retrieve()
                    .body(FoodInfoDto.class);
            if (dto == null) {
                throw new ProductNotFoundException("Product not found in database please add it manually or try again");
            }
            response = mapToResponse(dto);
            saveFood(response);
        }else{
            response.setFoodId(foodmodel.getFoodSecretId());
            response.setName(foodmodel.getName());
            response.setCalories(foodmodel.getCalories());
            response.setProtein(foodmodel.getProtein());
            response.setCarbohydrate(foodmodel.getCarbohydrate());
            response.setMetricServingAmount("100 g");
            response.setFat(foodmodel.getFat());
            response.setSugar(foodmodel.getSugar());
            response.setFiber(foodmodel.getFiber());
            response.setCholesterol(foodmodel.getCholesterol());
        }
        return response;
    }
    public FoodResponse mapToResponse(FoodInfoDto dto) {
        return dto.food().servings().servingList().stream()
                .filter(s -> "100 g".equalsIgnoreCase(s.description())
                        || ("g".equalsIgnoreCase(s.measurementDescription())
                        && "100.000".equals(s.metricServingAmount())))
                .findFirst()
                .map(s -> new FoodResponse(
                        dto.food().foodId(),
                        dto.food().name(),
                        s.metricServingAmount(),
                        parseBigDecimalSafe(s.calories()),
                        parseBigDecimalSafe(s.protein()),
                        parseBigDecimalSafe(s.carbohydrate()),
                        parseBigDecimalSafe(s.fat()),
                        parseBigDecimalSafe(s.sugar()),
                        parseBigDecimalSafe(s.fiber()),
                        parseBigDecimalSafe(s.cholesterol())
                ))
                .orElse(null);
    }
    public void saveFood(FoodResponse foodResponse) {
        FoodModel foodModel = new FoodModel();
        foodModel.setName(foodResponse.getName());
        foodModel.setFoodSecretId(foodResponse.getFoodId());
        foodModel.setCalories(foodResponse.getCalories());
        foodModel.setProtein(foodResponse.getProtein());
        foodModel.setCarbohydrate(foodResponse.getCarbohydrate());
        foodModel.setFiber(foodResponse.getFiber());
        foodModel.setSugar(foodResponse.getSugar());
        foodModel.setFat(foodResponse.getFat());
        foodModel.setCholesterol(foodResponse.getCholesterol());
        foodSecretRepository.save(foodModel);
    }
}