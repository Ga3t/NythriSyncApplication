package com.caliq.FoodSecretApiConnection.models;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="FOOD")
public class FoodModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Column(name="FOOD_SECRET_ID", nullable = false, unique = true)
    private String foodSecretId;

    @Column(name="NAME")
    private String name;

    @Column(name= "CALORIES")
    private BigDecimal calories;

    @Column(name="PROTEIN")
    private BigDecimal protein;

    @Column(name = "CARBOHYDRATE")
    private BigDecimal carbohydrate;

    @Column(name = "FAT")
    private BigDecimal fat;

    @Column(name = "SUGAR")
    private BigDecimal sugar;

    @Column(name = "FIBER")
    private BigDecimal fiber;

    @Column(name = "CHOLESTEROL")
    private BigDecimal cholesterol;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFoodSecretId() {
        return foodSecretId;
    }

    public void setFoodSecretId(String foodSecretId) {
        this.foodSecretId = foodSecretId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
