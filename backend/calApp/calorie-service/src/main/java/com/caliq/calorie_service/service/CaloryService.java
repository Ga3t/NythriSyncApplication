package com.caliq.calorie_service.service;


import com.caliq.calorie_service.models.dto.CalendarResponse;
import com.caliq.calorie_service.models.dto.MainPageResponseNew;
import com.caliq.calorie_service.models.entity.UserModel;
import com.caliq.calorie_service.models.response.MainPageResponse;
import com.caliq.calorie_service.models.response.MealByDateResponse;
import com.caliq.calorie_service.models.types.MealType;
import com.caliq.core.dto.MealDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public interface CaloryService{

    String saveMealToDb(Long userId, MealDto mealDto, LocalDate time, MealType mealType);
    MealByDateResponse getMealByDate(Long userId, LocalDate time,MealType mealType);
    MainPageResponse showMainPageInfo(Long userId);
    void saveToCaloryLogs(MealDto mealDto, LocalDate time, UserModel user);
    BigDecimal addWaterToLogs(Long userId, BigDecimal water, LocalDate date);
    MainPageResponseNew showMainPageInfoNew(Long userId);
    MainPageResponseNew showDatePageInfo(LocalDate date, Long userId);
    CalendarResponse getCalendar(int date, Long userId);
}
