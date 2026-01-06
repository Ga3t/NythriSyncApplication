package com.caliq.calorie_service.service;
import com.caliq.calorie_service.models.dto.UpdateUserDetailsDto;
import com.caliq.calorie_service.models.dto.UserDetailsDto;
import com.caliq.calorie_service.models.dto.UserInfoResponse;
import com.caliq.calorie_service.models.response.UserDetailsResponse;
import com.caliq.calorie_service.models.response.WeightLogsResponse;
import com.caliq.calorie_service.models.types.GoalType;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
@Service
public interface UserService {
    UserDetailsResponse saveUserDetails(UserDetailsDto userDetailsDto, Long userId);
    String updateUserDetails(UpdateUserDetailsDto userDetailsDto, Long userId);
    BigDecimal calculateForMale(BigDecimal height, BigDecimal weight, int ageRound, BigDecimal activityType);
    BigDecimal calculateForFemale(BigDecimal height, BigDecimal weight, int ageRound, BigDecimal activityType);
    BigDecimal goalTypeCalculate(BigDecimal bmr, GoalType goalType);
    BigDecimal calculateRecommendedWeightRange(BigDecimal heightCm);
    BigDecimal setNewWeight(BigDecimal weight, Long userId);
    Boolean detailsExists(Long userId);
    UserInfoResponse getUserInfo(Long userId);
}