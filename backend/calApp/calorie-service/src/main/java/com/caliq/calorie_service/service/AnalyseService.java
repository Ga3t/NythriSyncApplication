package com.caliq.calorie_service.service;
import com.caliq.calorie_service.models.entity.ReportEntity;
import com.caliq.calorie_service.models.entity.UserModel;
import com.caliq.calorie_service.models.entity.WeightLogsEntity;
import com.caliq.calorie_service.models.response.ReportResponse;
import com.caliq.core.dto.MealDto;
import com.caliq.core.message.InfoAboutProductMessage;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
public interface AnalyseService {
    ReportResponse analyseforDateRange(LocalDate startdate, LocalDate endDate, Long userId);
}