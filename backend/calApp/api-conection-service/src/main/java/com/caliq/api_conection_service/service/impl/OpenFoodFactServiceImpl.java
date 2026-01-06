package com.caliq.api_conection_service.service.impl;
import com.caliq.api_conection_service.exception.ProductNotFoundException;
import com.caliq.api_conection_service.model.AddFoodDto;
import com.caliq.api_conection_service.model.FoodDataResponse;
import com.caliq.api_conection_service.model.FoodEntity;
import com.caliq.api_conection_service.model.FoodInfoDto;
import com.caliq.api_conection_service.repository.OpenFoodFactRepository;
import com.caliq.api_conection_service.service.OpenFoodFactService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
@Service
@Primary
public class OpenFoodFactServiceImpl implements OpenFoodFactService {
    private final RestClient restClient;
    private OpenFoodFactRepository openFoodFactRepository;
    public OpenFoodFactServiceImpl(RestClient restClient, OpenFoodFactRepository openFoodFactRepository) {
        this.restClient = restClient;
        this.openFoodFactRepository = openFoodFactRepository;
    }
    @Cacheable(value = "foodDataCache", key = "#barcode")
    @Transactional
    public FoodDataResponse getFoodInfo(String barcode) {
        FoodEntity foodEntity = openFoodFactRepository
                .findById(Long.valueOf(barcode))
                .orElseGet(() -> {
                    String uri = "https://world.openfoodfacts.org/api/v2/product/{barcode}" +
                            "?fields=product_name,nutriscore_data,nutriments,allergens_tags";
                    FoodInfoDto dto = restClient.get()
                            .uri(uri, barcode)
                            .retrieve()
                            .body(FoodInfoDto.class);
                    if (dto == null || dto.getProduct() == null) {
                        throw new ProductNotFoundException("Product not found in OpenFoodFacts API, please add it manually or try again");
                    }
                    FoodEntity newEntity = new FoodEntity();
                    newEntity.setId(Long.valueOf(dto.getCode()));
                    newEntity.setName(dto.getProduct().getProductName());
                    FoodInfoDto.Nutriments n = dto.getProduct().getNutriments();
                    if (n != null) {
                        newEntity.setKcal(n.getEnergyKcal_100g());
                        newEntity.setProteins(n.getProteins_100g());
                        newEntity.setFat(n.getFat_100g());
                        newEntity.setCarbs(n.getCarbohydrates_100g());
                        newEntity.setFiber(n.getFiber_100g());
                        newEntity.setSugar(n.getSugars_100g());
                        newEntity.setCholesterol(n.getCholesterol_100g());
                    }
                    return openFoodFactRepository.save(newEntity);
                });
        return new FoodDataResponse(
                String.valueOf(foodEntity.getId()),
                foodEntity.getName(),
                foodEntity.getFat(),
                foodEntity.getProteins(),
                foodEntity.getCarbs(),
                foodEntity.getSugar(),
                foodEntity.getFiber(),
                foodEntity.getCholesterol(),
                foodEntity.getGrade(),
                null
        );
    }
    @Override
    @Transactional
    public FoodDataResponse addFoodByBarcode(AddFoodDto addFoodDto) {
        FoodEntity foodEntity =openFoodFactRepository
                .findById(Long.valueOf(addFoodDto.barcode()))
                .orElseGet(()->{
                    FoodEntity newFood = new FoodEntity(
                            Long.valueOf(addFoodDto.barcode()),
                            addFoodDto.brand_name(),
                            addFoodDto.name(),
                            addFoodDto.kcal(),
                            addFoodDto.proteins(),
                            addFoodDto.fat(),
                            addFoodDto.carbs(),
                            addFoodDto.fiber(),
                            addFoodDto.sugar(),
                            addFoodDto.cholesterol(),
                            addFoodDto.grade()
                    );
                    return openFoodFactRepository.save(newFood);
                });
        FoodDataResponse response = new FoodDataResponse(
                String.valueOf(foodEntity.getId()),
                foodEntity.getBrand_name()+" "+ foodEntity.getName(),
                foodEntity.getFat(),
                foodEntity.getProteins(),
                foodEntity.getCarbs(),
                foodEntity.getSugar(),
                foodEntity.getFiber(),
                foodEntity.getCholesterol(),
                foodEntity.getGrade(),
                null
        );
        return response;
    }
}