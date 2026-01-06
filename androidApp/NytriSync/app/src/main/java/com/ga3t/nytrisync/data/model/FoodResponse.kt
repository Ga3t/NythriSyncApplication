package com.ga3t.nytrisync.data.model
import java.math.BigDecimal
data class FoodResponse(
    val foodId: String,
    val name: String,
    val metricServingAmount: String?,
    val calories: BigDecimal,
    val protein: BigDecimal,
    val carbohydrate: BigDecimal,
    val fat: BigDecimal,
    val sugar: BigDecimal?,
    val fiber: BigDecimal?,
    val cholesterol: BigDecimal?
)