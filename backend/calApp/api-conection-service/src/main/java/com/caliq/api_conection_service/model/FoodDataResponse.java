package com.caliq.api_conection_service.model;
import java.math.BigDecimal;
public class FoodDataResponse {
    private String code;
    private String name;
    private BigDecimal fat;
    private BigDecimal protein;
    private BigDecimal carbohydrates;
    private BigDecimal sugar;
    private BigDecimal fiber;
    private BigDecimal cholesterol;
    private String nutriScore;
    private String[] allergens;
    public FoodDataResponse() {
    }
    public FoodDataResponse(String code, String name, BigDecimal fat, BigDecimal protein, BigDecimal carbohydrates, BigDecimal sugar, BigDecimal fiber, BigDecimal cholesterol, String nutriScore, String[] allergens) {
        this.code = code;
        this.name = name;
        this.fat = fat;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.sugar = sugar;
        this.fiber = fiber;
        this.cholesterol = cholesterol;
        this.nutriScore = nutriScore;
        this.allergens = allergens;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }
    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }
    public void setCarbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
    }
    public void setSugar(BigDecimal sugar) {
        this.sugar = sugar;
    }
    public void setFiber(BigDecimal fiber) {
        this.fiber = fiber;
    }
    public void setCholesterol(BigDecimal cholesterol) {
        this.cholesterol = cholesterol;
    }
    public void setNutriScore(String nutriScore) {
        this.nutriScore = nutriScore;
    }
    public void setAllergens(String[] allergens) {
        this.allergens = allergens;
    }
    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
    public BigDecimal getFat() {
        return fat;
    }
    public BigDecimal getProtein() {
        return protein;
    }
    public BigDecimal getCarbohydrates() {
        return carbohydrates;
    }
    public BigDecimal getSugar() {
        return sugar;
    }
    public BigDecimal getFiber() {
        return fiber;
    }
    public BigDecimal getCholesterol() {
        return cholesterol;
    }
    public String getNutriScore() {
        return nutriScore;
    }
    public String[] getAllergens() {
        return allergens;
    }
}