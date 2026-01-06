package com.ga3t.nytrisync.data.model
import java.math.BigDecimal
enum class SexType { MALE, FEMALE }
enum class GoalType { LOSS, GAIN, MAINTENANCE }
data class UserDetailsDto(
    val currentWeight: BigDecimal,
    val birthDay: String,
    val sex: SexType,
    val activityType: BigDecimal,
    val goalType: GoalType,
    val wantedWeight: BigDecimal,
    val height: BigDecimal
)
data class UserDetailsResponse(
    val avgweight: BigDecimal,
    val bmr: BigDecimal
)