package com.ga3t.nytrisync.data.model
import java.math.BigDecimal
data class FoodDataResponse(
    val code: String,
    val name: String,
    val fat: BigDecimal,
    val protein: BigDecimal,
    val carbohydrates: BigDecimal,
    val sugar: BigDecimal?,
    val fiber: BigDecimal?,
    val cholesterol: BigDecimal?,
    val nutriScore: String?,
    val allergens: List<String>?
)