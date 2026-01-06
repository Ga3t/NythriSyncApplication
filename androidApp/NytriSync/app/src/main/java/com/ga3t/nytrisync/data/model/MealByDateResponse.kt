package com.ga3t.nytrisync.data.model
data class MealByDateResponse(
    val date: String,
    val mealType: MealType,
    val mealDto: MealDto?
)