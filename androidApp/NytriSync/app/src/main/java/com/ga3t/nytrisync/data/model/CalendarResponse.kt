package com.ga3t.nytrisync.data.model
import java.math.BigDecimal
data class CalendarResponse(
    val calendar: List<CaloryDays>
) {
    data class CaloryDays(
        val date: String,
        val caloryNorm: BigDecimal,
        val caloryCons: BigDecimal
    )
}