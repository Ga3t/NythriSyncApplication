package com.caliq.calorie_service.models.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
public record UserInfoResponse(BigDecimal weight,
                               BigDecimal height,
                               String sex,
                               String activity_type,
                               LocalDate birthday_date,
                               String goalType) {
}