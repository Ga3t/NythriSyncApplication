package com.caliq.calorie_service.models.dto;
import com.caliq.calorie_service.models.types.GoalType;
import com.caliq.calorie_service.models.types.SexType;
import java.math.BigDecimal;
import java.time.LocalDate;
public record UpdateUserDetailsDto(BigDecimal curentWeight,
                                   LocalDate birthDay,
                                   SexType sex,
                                   BigDecimal activityType,
                                   GoalType goalType,
                                   BigDecimal wantedWeight,
                                   BigDecimal height) {}