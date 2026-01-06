package com.caliq.calorie_service.models.dto;
import com.caliq.calorie_service.models.types.GoalType;
import com.caliq.calorie_service.models.types.SexType;
import org.springframework.lang.NonNull;
import java.math.BigDecimal;
import java.time.LocalDate;
public record UserDetailsDto(
        @NonNull
        BigDecimal currentWeight,
        @NonNull
        LocalDate birthDay,
        @NonNull
        SexType sex,
        @NonNull
        BigDecimal activityType,
        @NonNull
        GoalType goalType,
        @NonNull
        BigDecimal wantedWeight,
        @NonNull
        BigDecimal height
) {
}