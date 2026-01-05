package com.ga3t.nytrisync.data.model

import java.math.BigDecimal

data class UserInfoResponse(
    val weight: BigDecimal,
    val height: BigDecimal,
    val sex: String,
    val activity_type: String,
    val birthday_date: String,
    val goalType: String
)