package com.ga3t.nytrisync.data.model
import java.math.BigDecimal
data class UpdateUserDetailsDto(
    val curentWeight: BigDecimal,
    val birthDay: String,
    val sex: String,
    val activityType: BigDecimal,
    val goalType: String,
    val wantedWeight: BigDecimal,
    val height: BigDecimal
)