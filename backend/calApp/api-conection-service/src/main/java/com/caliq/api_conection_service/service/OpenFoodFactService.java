package com.caliq.api_conection_service.service;
import com.caliq.api_conection_service.model.AddFoodDto;
import com.caliq.api_conection_service.model.FoodDataResponse;
import com.caliq.api_conection_service.model.FoodInfoDto;
import org.springframework.stereotype.Service;
@Service
public interface OpenFoodFactService {
    FoodDataResponse getFoodInfo(String barcode);
    FoodDataResponse addFoodByBarcode(AddFoodDto addFoodDto);
}