package com.ga3t.nytrisync.data.model
data class FoodSearchResponse(
    val items: List<FoodItem>,
    val totalResults: String
) {
    data class FoodItem(
        val id: String,
        val name: String,
        val description: String
    )
}