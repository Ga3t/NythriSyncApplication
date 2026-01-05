package com.caliq.calorie_service.models.entity;


import com.caliq.calorie_service.models.types.GoalType;
import com.caliq.calorie_service.models.types.SexType;
import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER_DETAILS")
public class UserModel {

    @Id
    @Column(name="USER_ID", unique = true)
    private Long id;

    @Column(name="BIRTH_DAY", nullable = false)
    private LocalDate birthDay;

    @Column(name="HEIGHT", nullable = false)
    private BigDecimal height;

    @Column(name="SEX", nullable = false)
    @Enumerated(EnumType.STRING)
    private SexType sex;

    @Column(name="ACTIVITY_TYPE")
    private BigDecimal activityType;

    @Column(name="CURRENT_WEIGHT")
    private BigDecimal currentWeight;

    @Column(name = "GOAL_TYPE")
    private GoalType goalType;

    @Column(name="WANTED_WEIGHT", nullable = false)
    private BigDecimal wantedWeight;

    @Column(name="RECOMMENDED_WEIGHT",nullable = false)
    private BigDecimal recommendedWeight;

    @Column(name="WATER_NEEDS_ML")
    private BigDecimal waterNeeds;

    @Column(name="BMR")
    private BigDecimal bmr;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealEntity> meals = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(BigDecimal currentWeight) {
        this.currentWeight = currentWeight;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public BigDecimal getWantedWeight() {
        return wantedWeight;
    }

    public void setWantedWeight(BigDecimal wantedWeight) {
        this.wantedWeight = wantedWeight;
    }

    public BigDecimal getRecommendedWeight() {
        return recommendedWeight;
    }

    public void setRecommendedWeight(BigDecimal recommendedWeight) {
        this.recommendedWeight = recommendedWeight;
    }

    public List<MealEntity> getMeals() {
        return meals;
    }

    public void setMeals(List<MealEntity> meals) {
        this.meals = meals;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public SexType getSex() {
        return sex;
    }

    public void setSex(SexType sex) {
        this.sex = sex;
    }

    public BigDecimal getActivityType() {
        return activityType;
    }

    public void setActivityType(BigDecimal activityType) {
        this.activityType = activityType;
    }

    public BigDecimal getBmr() {
        return bmr;
    }

    public void setBmr(BigDecimal bmr) {
        this.bmr = bmr;
    }

    public BigDecimal getWaterNeeds() {
        return waterNeeds;
    }

    public void setWaterNeeds(BigDecimal waterNeeds) {
        this.waterNeeds = waterNeeds;
    }
}
