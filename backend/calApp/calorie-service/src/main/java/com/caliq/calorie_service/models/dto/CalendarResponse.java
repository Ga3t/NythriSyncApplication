package com.caliq.calorie_service.models.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CalendarResponse(List<CaloryDays> calendar) {

    public record CaloryDays(LocalDate date,
                             BigDecimal caloryNorm,
                             BigDecimal caloryCons){}
}
