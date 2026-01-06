package com.ga3t.nytrisync.data.model
import java.math.BigDecimal
data class ReportResponse(
    val anlyses: List<DayAnalyse>
) {
    data class DayAnalyse(
        val date: String,
        val weight: BigDecimal?,
        val sugarCons: BigDecimal,
        val sugarNorm: BigDecimal,
        val fiberCons: BigDecimal,
        val fiberNorm: BigDecimal,
        val kcalCons: BigDecimal,
        val kcalNorm: BigDecimal,
        val fatCons: BigDecimal,
        val fatNorm: BigDecimal,
        val proteinCons: BigDecimal,
        val proteinNorm: BigDecimal,
        val carbsCons: BigDecimal,
        val carbsNorm: BigDecimal,
        val waterCons: BigDecimal,
        val waterNorm: BigDecimal
    )
}