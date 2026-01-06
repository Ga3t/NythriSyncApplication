package com.caliq.calorie_service.models.entity;
import com.caliq.calorie_service.models.types.MealType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
@Entity
@Table(name = "MEAL_TABLE")
public class MealEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserModel user;
    @Enumerated(EnumType.STRING)
    @Column(name="MEAL_TYPE")
    private MealType mealType;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "MEALS", columnDefinition = "json")
    private JsonNode meal;
    @Column(name="DATETIME")
    private LocalDate dateTime;
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
    public MealType getMealType() {
        return mealType;
    }
    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
    public JsonNode getMeal() {
        return meal;
    }
    public void setMeal(JsonNode meal) {
        this.meal = meal;
    }
    public LocalDate getDateTime() {
        return dateTime;
    }
    public void setDateTime(LocalDate dateTime) {
        this.dateTime = dateTime;
    }
}