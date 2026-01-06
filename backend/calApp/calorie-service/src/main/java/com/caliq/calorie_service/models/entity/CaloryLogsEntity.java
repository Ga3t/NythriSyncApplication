package com.caliq.calorie_service.models.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name="CALORY_LOGS")
public class CaloryLogsEntity{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserModel user;
    @Column(name="TOTAL_CALORY")
    private BigDecimal totalCalory;
    @Column(name="FAT")
    private BigDecimal fat;
    @Column(name = "PROREIN")
    private BigDecimal protein;
    @Column(name="CARBOHYDRATES")
    private BigDecimal carbohydrates;
    @Column(name="DRANK_WATER_ML")
    private BigDecimal drankWaterMl;
    @Column(name="FIBER")
    private BigDecimal fiber;
    @Column(name="SUGAR")
    private BigDecimal sugar;
    @Column(name="DATE", nullable = false)
    private LocalDate date;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public UserModel getUser() {
        return user;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public BigDecimal getTotalCalory() {
        return totalCalory;
    }
    public void setTotalCalory(BigDecimal totalCalory) {
        this.totalCalory = totalCalory;
    }
    public BigDecimal getDrankWaterMl() {
        return drankWaterMl;
    }
    public void setDrankWaterMl(BigDecimal drankWaterMl) {
        this.drankWaterMl = drankWaterMl;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public BigDecimal getFat() {
        return fat;
    }
    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }
    public BigDecimal getProtein() {
        return protein;
    }
    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }
    public BigDecimal getCarbohydrates() {
        return carbohydrates;
    }
    public void setCarbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
    }
    public BigDecimal getFiber() {return fiber;}
    public void setFiber(BigDecimal fiber) {this.fiber = fiber;}
    public BigDecimal getSugar() {return sugar;}
    public void setSugar(BigDecimal sugar) {this.sugar = sugar;}
}