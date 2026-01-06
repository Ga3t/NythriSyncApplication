package com.caliq.core.response;
import java.math.BigDecimal;
public class FoodResponse {
    private String foodId;
    private String name;
    private String metricServingAmount;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal carbohydrate;
    private BigDecimal fat;
    private BigDecimal sugar;
    private BigDecimal fiber;
    private BigDecimal cholesterol;
    public FoodResponse() {}
    public FoodResponse(String foodId, String name, String metricServingAmount, BigDecimal calories, BigDecimal protein, BigDecimal carbohydrate, BigDecimal fat, BigDecimal sugar, BigDecimal fiber, BigDecimal cholesterol) {
        this.foodId = foodId;
        this.name = name;
        this.metricServingAmount = metricServingAmount;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrate = carbohydrate;
        this.fat = fat;
        this.sugar = sugar;
        this.fiber = fiber;
        this.cholesterol = cholesterol;
    }
    public String getFoodId() {
        return foodId;
    }
    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMetricServingAmount() {
        return metricServingAmount;
    }
    public void setMetricServingAmount(String metricServingAmount) {
        this.metricServingAmount = metricServingAmount;
    }
    public BigDecimal getCalories() {
        return calories;
    }
    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }
    public BigDecimal getProtein() {
        return protein;
    }
    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }
    public BigDecimal getCarbohydrate() {
        return carbohydrate;
    }
    public void setCarbohydrate(BigDecimal carbohydrate) {
        this.carbohydrate = carbohydrate;
    }
    public BigDecimal getFat() {
        return fat;
    }
    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }
    public BigDecimal getSugar() {
        return sugar;
    }
    public void setSugar(BigDecimal sugar) {
        this.sugar = sugar;
    }
    public BigDecimal getFiber() {
        return fiber;
    }
    public void setFiber(BigDecimal fiber) {
        this.fiber = fiber;
    }
    public BigDecimal getCholesterol() {
        return cholesterol;
    }
    public void setCholesterol(BigDecimal cholesterol) {
        this.cholesterol = cholesterol;
    }
}
