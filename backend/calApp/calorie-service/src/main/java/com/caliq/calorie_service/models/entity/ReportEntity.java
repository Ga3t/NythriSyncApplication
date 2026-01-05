package com.caliq.calorie_service.models.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="reports")
public class ReportEntity {

    public ReportEntity() {
    }

   public ReportEntity(LocalDate date, UserModel userModel,
                       BigDecimal drinkedWater, BigDecimal consumedKcal,
                       BigDecimal consumedCarbs, BigDecimal consumedProtein,
                       BigDecimal consumedFat, BigDecimal consumedSugars,
                       BigDecimal consumedFiber){
        this.date = date;
        this.user= userModel;
        this.drinkedWater=drinkedWater;
        this.consumedKcal= consumedKcal;
        this.consumedCarbs=consumedCarbs;
        this.consumedProtein=consumedProtein;
        this.consumedFat= consumedFat;
        this.consumedSugars= consumedSugars;
        this.consumedFiber= consumedFiber;

   }

    public ReportEntity(UserModel user, LocalDate date) {
        this.date = date;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    LocalDate date;

    @Column()
    BigDecimal weight;

    @Column()
    BigDecimal drinkedWater;

    @Column()
    BigDecimal waterNeeds;

    @Column()
    BigDecimal consumedKcal;

    @Column()
    BigDecimal kcalNeeds;

    @Column
    BigDecimal consumedFat;

    @Column
    BigDecimal consumedProtein;

    @Column
    BigDecimal consumedCarbs;

    @Column()
    BigDecimal consumedSugars;

    @Column()
    BigDecimal normalSugar;

    @Column()
    BigDecimal consumedFiber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserModel user;

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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getDrinkedWater() {
        return drinkedWater;
    }

    public void setDrinkedWater(BigDecimal drinkedWater) {
        this.drinkedWater = drinkedWater;
    }

    public BigDecimal getWaterNeeds() {
        return waterNeeds;
    }

    public void setWaterNeeds(BigDecimal waterNeeds) {
        this.waterNeeds = waterNeeds;
    }

    public BigDecimal getConsumedKcal() {
        return consumedKcal;
    }

    public void setConsumedKcal(BigDecimal consumedKcal) {
        this.consumedKcal = consumedKcal;
    }

    public BigDecimal getKcalNeeds() {
        return kcalNeeds;
    }

    public void setKcalNeeds(BigDecimal kcalNeeds) {
        this.kcalNeeds = kcalNeeds;
    }

    public BigDecimal getConsumedFat() {
        return consumedFat;
    }

    public void setConsumedFat(BigDecimal consumedFat) {
        this.consumedFat = consumedFat;
    }

    public BigDecimal getConsumedProtein() {
        return consumedProtein;
    }

    public void setConsumedProtein(BigDecimal consumedProtein) {
        this.consumedProtein = consumedProtein;
    }

    public BigDecimal getConsumedCarbs() {
        return consumedCarbs;
    }

    public void setConsumedCarbs(BigDecimal consumedCarbs) {
        this.consumedCarbs = consumedCarbs;
    }

    public BigDecimal getConsumedSugars() {
        return consumedSugars;
    }

    public void setConsumedSugars(BigDecimal consumedSugars) {
        this.consumedSugars = consumedSugars;
    }

    public BigDecimal getNormalSugar() {
        return normalSugar;
    }

    public void setNormalSugar(BigDecimal normalSugar) {
        this.normalSugar = normalSugar;
    }

    public BigDecimal getConsumedFiber() {
        return consumedFiber;
    }

    public void setConsumedFiber(BigDecimal consumedFiber) {
        this.consumedFiber = consumedFiber;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
