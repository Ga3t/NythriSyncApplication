package com.caliq.calorie_service.models.entity;


import com.caliq.calorie_service.models.types.GoalType;
import com.caliq.calorie_service.models.types.SexType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="weight_logs")
public class WeightLogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    private LocalDate date;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserModel user;

    public WeightLogsEntity() {
    }

    public WeightLogsEntity(LocalDate date, BigDecimal height, SexType sex,
                            BigDecimal activityType, BigDecimal currentWeight, GoalType goalType,
                            BigDecimal wantedWeight, BigDecimal recommendedWeight, BigDecimal waterNeeds,
                            BigDecimal bmr, UserModel user) {
        this.date = date;
        this.height = height;
        this.sex = sex;
        this.activityType = activityType;
        this.currentWeight = currentWeight;
        this.goalType = goalType;
        this.wantedWeight = wantedWeight;
        this.recommendedWeight = recommendedWeight;
        this.waterNeeds = waterNeeds;
        this.bmr = bmr;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public BigDecimal getWaterNeeds() {
        return waterNeeds;
    }

    public void setWaterNeeds(BigDecimal waterNeeds) {
        this.waterNeeds = waterNeeds;
    }

    public BigDecimal getBmr() {
        return bmr;
    }

    public void setBmr(BigDecimal bmr) {
        this.bmr = bmr;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
