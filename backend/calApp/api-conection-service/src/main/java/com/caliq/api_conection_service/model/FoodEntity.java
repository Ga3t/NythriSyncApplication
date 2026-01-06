package com.caliq.api_conection_service.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
@Entity
@Table(name="PRODUCT")
public class FoodEntity {
    @Id
    @Column(name="ID", unique = true, nullable = false)
    private Long id;
    @Column(name="BRAND_NAME")
    private String brand_name;
    @Column(name="NAME")
    private String name;
    @Column(name="KCAL")
    private BigDecimal kcal;
    @Column(name="PROTEINS")
    private BigDecimal proteins;
    @Column(name="FAT")
    private BigDecimal fat;
    @Column(name="CARBS")
    private BigDecimal carbs;
    @Column(name="FIBER")
    private BigDecimal fiber;
    @Column(name="SUGAR")
    private BigDecimal sugar;
    @Column(name="CHOLESTEROL")
    private BigDecimal cholesterol;
    @Column(name="GRADE")
    private String grade;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBrand_name() {
        return brand_name;
    }
    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigDecimal getKcal() {
        return kcal;
    }
    public void setKcal(BigDecimal kcal) {
        this.kcal = kcal;
    }
    public BigDecimal getProteins() {
        return proteins;
    }
    public void setProteins(BigDecimal proteins) {
        this.proteins = proteins;
    }
    public BigDecimal getFat() {
        return fat;
    }
    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }
    public BigDecimal getCarbs() {
        return carbs;
    }
    public void setCarbs(BigDecimal carbs) {
        this.carbs = carbs;
    }
    public BigDecimal getFiber() {
        return fiber;
    }
    public void setFiber(BigDecimal fiber) {
        this.fiber = fiber;
    }
    public BigDecimal getSugar() {
        return sugar;
    }
    public void setSugar(BigDecimal sugar) {
        this.sugar = sugar;
    }
    public BigDecimal getCholesterol() {
        return cholesterol;
    }
    public void setCholesterol(BigDecimal cholesterol) {
        this.cholesterol = cholesterol;
    }
    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public FoodEntity(Long id, String brand_name, String name, BigDecimal kcal, BigDecimal proteins, BigDecimal fat, BigDecimal carbs, BigDecimal fiber, BigDecimal sugar, BigDecimal cholesterol, String grade) {
        this.id = id;
        this.brand_name = brand_name;
        this.name = name;
        this.kcal = kcal;
        this.proteins = proteins;
        this.fat = fat;
        this.carbs = carbs;
        this.fiber = fiber;
        this.sugar = sugar;
        this.cholesterol = cholesterol;
        this.grade = grade;
    }
    public FoodEntity() {
    }
}