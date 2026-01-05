package com.caliq.api_conection_service.controller;


import com.caliq.api_conection_service.model.AddFoodDto;
import com.caliq.api_conection_service.model.FoodDataResponse;
import com.caliq.api_conection_service.model.FoodInfoDto;
import com.caliq.api_conection_service.service.OpenFoodFactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class OpenFoodFactController {

    private final OpenFoodFactService openFoodFactService;

    public OpenFoodFactController(OpenFoodFactService openFoodFactService) {
        this.openFoodFactService = openFoodFactService;
    }


    @GetMapping("/findbybarcode/{barcode}")
    public ResponseEntity<FoodDataResponse> getInfoByBarcode(@PathVariable("barcode") String barcode){

        FoodDataResponse response = openFoodFactService.getFoodInfo(barcode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/addbarcode/{barcode}")
    public ResponseEntity<FoodDataResponse> addFoodByBarcode(@RequestBody AddFoodDto addFoodDto){
        return  ResponseEntity.ok(openFoodFactService.addFoodByBarcode(addFoodDto));
    }

}
