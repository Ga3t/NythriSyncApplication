package com.caliq.calorie_service.service.impl;

import com.caliq.calorie_service.exeptions.UserNotFoundException;
import com.caliq.calorie_service.models.entity.CaloryLogsEntity;
import com.caliq.calorie_service.models.entity.UserModel;
import com.caliq.calorie_service.models.entity.WeightLogsEntity;
import com.caliq.calorie_service.models.response.ReportResponse;
import com.caliq.calorie_service.repository.*;
import com.caliq.calorie_service.service.AnalyseService;
import com.caliq.calorie_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Slf4j
@Service
@Primary
public class AnalyseServiceImpl implements AnalyseService {
    private AnalyseRepository analyseRepository;
    private WeightLogsRepository weightLogsRepository;
    private CaloryLogsRepository caloryLogsRepository;
    private UserRepository userRepository;
    private UserService userService;

    public AnalyseServiceImpl(AnalyseRepository analyseRepository, WeightLogsRepository weightLogsRepository, CaloryLogsRepository caloryLogsRepository, UserRepository userRepository, UserService userService) {
        this.analyseRepository = analyseRepository;
        this.weightLogsRepository = weightLogsRepository;
        this.caloryLogsRepository = caloryLogsRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public ReportResponse analyseforDateRange(LocalDate startDate, LocalDate endDate, Long userId) {


        List<CaloryLogsEntity> caloryLogs = caloryLogsRepository
                .findAllByUser_IdAndDateBetweenOrderByDateAsc(userId, startDate, endDate);


        List<WeightLogsEntity> weightLogs = weightLogsRepository
                .findAllByUser_IdAndDateBetweenOrderByDateAsc(
                        userId, startDate.minusWeeks(2), endDate.plusWeeks(2));


        UserModel userModel = null;
        if (endDate.equals(LocalDate.now())) {
            userModel = userRepository.findById(userId)
                    .orElseThrow(()-> new UserNotFoundException("User not found!"));
        }

        List<ReportResponse.DayAnalyse> analyses = new ArrayList<>();

        for (CaloryLogsEntity cal : caloryLogs) {
            LocalDate date = cal.getDate();
            WeightLogsEntity weightLog = findLastWeightLogOnOrBeforeDate(weightLogs, date);

            BigDecimal weight = weightLog != null ? weightLog.getCurrentWeight() :
                    (userModel != null ? userModel.getCurrentWeight() : null);


            BigDecimal bmrNorm   = getSafe(
                    weightLog != null ? weightLog.getBmr()
                            : userModel != null ? userModel.getBmr() : BigDecimal.valueOf(2500));
            BigDecimal allFpc = cal.getCarbohydrates().add(cal.getFat()).add(cal.getProtein());
            BigDecimal waterNorm = getSafe(
                    weightLog != null ? weightLog.getWaterNeeds()
                            : userModel != null ? userModel.getWaterNeeds() : BigDecimal.valueOf(2000));

            ReportResponse.DayAnalyse day = new ReportResponse.DayAnalyse(
                    date,
                    weight,
                    nz(cal.getSugar()),
                    nz(cal.getTotalCalory()).multiply(BigDecimal.valueOf(0.05)),
                    nz(cal.getFiber()),
                    BigDecimal.valueOf(30),
                    nz(cal.getTotalCalory()),
                    bmrNorm,
                    nz(cal.getFat()),
                    allFpc.multiply(BigDecimal.valueOf(0.3)),
                    nz(cal.getProtein()),
                    (weight != null ? weight.multiply(BigDecimal.valueOf(1.2)) : BigDecimal.ZERO),
                    nz(cal.getCarbohydrates()),
                    allFpc.multiply(BigDecimal.valueOf(0.4)),
                    nz(cal.getDrankWaterMl()),
                    waterNorm
            );

            analyses.add(day);
        }

        return new ReportResponse(analyses);
    }


    private WeightLogsEntity findLastWeightLogOnOrBeforeDate(List<WeightLogsEntity> weightLogs,
                                                             LocalDate targetDate) {
        WeightLogsEntity last = null;
        for (WeightLogsEntity w : weightLogs) {
            if (!w.getDate().isAfter(targetDate)) {
                last = w;
            } else {
                break;
            }
        }
        return last;
    }


    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private BigDecimal getSafe(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

}
