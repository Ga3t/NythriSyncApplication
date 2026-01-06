package com.caliq.calorie_service.models.dto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
public record MainPageResponseNew(
        WeekCalory weekCalory,
        TodayCalory todayCalory,
        TodayWater todayWater,
        TodayCarbs todayCarbs,
        TodayProtein todayProtein,
        TodayFat todayFat,
        List<MealPage> mealPage
) {
    public record WeekCalory(Map<String, BigDecimal>thisWeekCaloryCons,
                              Map<String, BigDecimal>thisWeekCaloryNorm){}
    public record  TodayCalory(BigDecimal todayCaloryCons,
                               BigDecimal todayCaloryNorm){}
    public record TodayWater(BigDecimal todayWaterCons,
                             BigDecimal todayWaterNeeds){}
    public record TodayCarbs(BigDecimal todayCarbsCons,
                             BigDecimal todayCarbsNorm){}
    public record TodayProtein(BigDecimal todayProteinCons,
                               BigDecimal todayProteinNorm){}
    public record TodayFat(BigDecimal todayFatCons,
                           BigDecimal todayFatNorm){}
    public record MealPage(String mealType,
                           BigDecimal caloryCons){}
}