package com.ga3t.nytrisync.data.model

import java.math.BigDecimal

data class MainPageResponse(
    val weekCalory: WeekCalory,
    val todayCalory: TodayCalory,
    val todayWater: TodayWater,
    val todayCarbs: TodayCarbs,
    val todayProtein: TodayProtein,
    val todayFat: TodayFat,
    val mealPage: List<MealPage>
) {
    data class WeekCalory(
        val thisWeekCaloryCons: Map<String, BigDecimal>,
        val thisWeekCaloryNorm: Map<String, BigDecimal>
    )
    data class TodayCalory(
        val todayCaloryCons: BigDecimal,
        val todayCaloryNorm: BigDecimal
    )
    data class TodayWater(
        val todayWaterCons: BigDecimal,
        val todayWaterNeeds: BigDecimal
    )
    data class TodayCarbs(
        val todayCarbsCons: BigDecimal,
        val todayCarbsNorm: BigDecimal
    )
    data class TodayProtein(
        val todayProteinCons: BigDecimal,
        val todayProteinNorm: BigDecimal
    )
    data class TodayFat(
        val todayFatCons: BigDecimal,
        val todayFatNorm: BigDecimal
    )
    data class MealPage(
        val mealType: String,
        val caloryCons: BigDecimal
    )
}