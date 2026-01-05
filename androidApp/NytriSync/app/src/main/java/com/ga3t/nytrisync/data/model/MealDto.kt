package com.ga3t.nytrisync.data.model

import java.math.BigDecimal

data class MealDto(
    val dishes: Dishes
) {
    data class Dishes(
        val dish: List<Dish>
    )

    data class Dish(
        val type: String,
        val code: String,
        val name: String,
        val calories: BigDecimal,
        val fat: BigDecimal,
        val protein: BigDecimal,
        val carbohydrates: BigDecimal,
        val sugars: BigDecimal,
        val fiber: BigDecimal
    )
}