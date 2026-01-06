package com.caliq.calorie_service.models.response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public record ReportResponse(List<DayAnalyse> anlyses){
    public record DayAnalyse(LocalDate date, BigDecimal weight,
                             BigDecimal sugarCons, BigDecimal sugarNorm,
                             BigDecimal fiberCons, BigDecimal fiberNorm,
                             BigDecimal kcalCons, BigDecimal kcalNorm,
                             BigDecimal fatCons, BigDecimal fatNorm,
                             BigDecimal proteinCons, BigDecimal proteinNorm,
                             BigDecimal carbsCons, BigDecimal carbsNorm,
                             BigDecimal waterCons, BigDecimal waterNorm){
    }
}