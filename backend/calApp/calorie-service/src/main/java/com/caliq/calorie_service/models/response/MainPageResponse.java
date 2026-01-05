package com.caliq.calorie_service.models.response;

import java.math.BigDecimal;
import java.util.List;

public class MainPageResponse {

    private BigDecimal calorieValue;
    private BigDecimal calorieGoal;
    private BigDecimal fatValue;
    private BigDecimal proteinValue;
    private BigDecimal carbohydrateValue;
    private BigDecimal waterValue;
    private BigDecimal waterGoal;
    private BigDecimal currentWeight;

    public BigDecimal getCalorieValue() {
        return calorieValue;
    }

    public void setCalorieValue(BigDecimal calorieValue) {
        this.calorieValue = calorieValue;
    }

    public BigDecimal getFatValue() {
        return fatValue;
    }

    public void setFatValue(BigDecimal fatValue) {
        this.fatValue = fatValue;
    }

    public BigDecimal getProteinValue() {
        return proteinValue;
    }

    public void setProteinValue(BigDecimal proteinValue) {
        this.proteinValue = proteinValue;
    }

    public BigDecimal getCarbohydrateValue() {
        return carbohydrateValue;
    }

    public void setCarbohydrateValue(BigDecimal carbohydrateValue) {
        this.carbohydrateValue = carbohydrateValue;
    }

    public BigDecimal getWaterValue() {
        return waterValue;
    }

    public void setWaterValue(BigDecimal waterValue) {
        this.waterValue = waterValue;
    }

    public BigDecimal getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(BigDecimal currentWeight) {
        this.currentWeight = currentWeight;
    }

    public BigDecimal getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(BigDecimal calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public BigDecimal getWaterGoal() {
        return waterGoal;
    }

    public void setWaterGoal(BigDecimal waterGoal) {
        this.waterGoal = waterGoal;
    }
}
