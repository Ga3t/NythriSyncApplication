package com.caliq.FoodSecretApiConnection.controller;


import com.caliq.core.response.FoodResponse;
import com.caliq.FoodSecretApiConnection.models.FoodSearchResponse;
import com.caliq.FoodSecretApiConnection.service.FoodSecretApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/foodsecret")
public class FoodSecretApiController {

    private FoodSecretApiService foodSecretApiService;

    public FoodSecretApiController(FoodSecretApiService foodSecretApiService) {
        this.foodSecretApiService = foodSecretApiService;
    }

    @GetMapping("/search")
    public ResponseEntity<FoodSearchResponse> search(@RequestParam("search_expression")  String search_expression,
                                                    @RequestParam(name = "page", defaultValue = "0") int page) {

        FoodSearchResponse foodSearchResponse = foodSecretApiService.getFoodSearchResponse(search_expression, page);
        return ResponseEntity.ok(foodSearchResponse);
    }

    @GetMapping("/details")
    public ResponseEntity<FoodResponse> details(@RequestParam("id") String id) {
        FoodResponse foodResponse = foodSecretApiService.getFoodResponse(id);
        return ResponseEntity.ok(foodResponse);
    }

}
