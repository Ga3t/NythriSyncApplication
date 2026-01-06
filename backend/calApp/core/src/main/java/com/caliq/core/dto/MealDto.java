package com.caliq.core.dto;
import java.math.BigDecimal;
import java.util.List;
public record MealDto(Dishes dishes) {
    public record Dishes(List<Dish> dish) {
    }
    public record Dish(
            String type,
            String code,
            String name,
            BigDecimal calories,
            BigDecimal fat,
            BigDecimal protein,
            BigDecimal carbohydrates,
            BigDecimal sugars,
            BigDecimal fiber
    ) {
    }
}
