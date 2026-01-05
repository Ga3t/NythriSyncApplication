package com.caliq.calorie_service.service.impl;


import com.caliq.calorie_service.exeptions.UserNotFoundException;
import com.caliq.calorie_service.models.dto.UpdateUserDetailsDto;
import com.caliq.calorie_service.models.dto.UserDetailsDto;
import com.caliq.calorie_service.models.dto.UserInfoResponse;
import com.caliq.calorie_service.models.entity.WeightLogsEntity;
import com.caliq.calorie_service.models.response.UserDetailsResponse;
import com.caliq.calorie_service.models.entity.UserModel;
import com.caliq.calorie_service.models.types.GoalType;
import com.caliq.calorie_service.models.types.SexType;
import com.caliq.calorie_service.repository.UserRepository;
import com.caliq.calorie_service.repository.WeightLogsRepository;
import com.caliq.calorie_service.service.UserService;
import com.caliq.core.message.WeightMessage;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
@Primary
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private WeightLogsRepository weightLogsRepository;

    public UserServiceImpl(UserRepository userRepository, WeightLogsRepository weightLogsRepository) {
        this.userRepository = userRepository;
        this.weightLogsRepository= weightLogsRepository;
    }

    @Override
    @Transactional
    public UserDetailsResponse saveUserDetails(UserDetailsDto userDetailsDto, Long userId) {
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        UserModel userModel = new UserModel();
        userModel.setId(userId);
        userModel.setActivityType(userDetailsDto.activityType());
        userModel.setBirthDay(userDetailsDto.birthDay());
        userModel.setHeight(userDetailsDto.height());
        userModel.setSex(userDetailsDto.sex());
        userModel.setCurrentWeight(userDetailsDto.currentWeight());
        userModel.setGoalType(userDetailsDto.goalType());
        userModel.setWantedWeight(userDetailsDto.wantedWeight());


        int age = Period.between(userDetailsDto.birthDay(), LocalDate.now()).getYears();

        BigDecimal tdee;

        if (userDetailsDto.sex() == SexType.MALE) {
            tdee = calculateForMale(
                    userDetailsDto.height(),
                    userDetailsDto.currentWeight(),
                    age,
                    userDetailsDto.activityType()
            );
        } else {
            tdee = calculateForFemale(
                    userDetailsDto.height(),
                    userDetailsDto.currentWeight(),
                    age,
                    userDetailsDto.activityType()
            );
        }

        BigDecimal waterNeedsMl = userDetailsDto.currentWeight().multiply(BigDecimal.valueOf(35));
        userModel.setWaterNeeds(waterNeedsMl);

        BigDecimal caloriesTarget = goalTypeCalculate(tdee, userDetailsDto.goalType());
        userModel.setBmr(caloriesTarget);
        BigDecimal recommendedWeight = calculateRecommendedWeightRange(userDetailsDto.height());
        userModel.setRecommendedWeight(recommendedWeight);
        userRepository.save(userModel);
        userDetailsResponse.setBmr(caloriesTarget);
        userDetailsResponse.setAvgweight(recommendedWeight);

        return userDetailsResponse;
    }

    @Override
    public BigDecimal calculateForMale(BigDecimal height, BigDecimal weight, int ageRound, BigDecimal activityFactor) {
        BigDecimal bmr = BigDecimal.valueOf(10).multiply(weight)
                .add(BigDecimal.valueOf(6.25).multiply(height))
                .subtract(BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(ageRound)))
                .add(BigDecimal.valueOf(5));

        return bmr.multiply(activityFactor);
    }

    @Override
    public BigDecimal calculateForFemale(BigDecimal height, BigDecimal weight, int ageRound, BigDecimal activityFactor) {
        BigDecimal bmr = BigDecimal.valueOf(10).multiply(weight)
                .add(BigDecimal.valueOf(6.25).multiply(height))
                .subtract(BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(ageRound)))
                .subtract(BigDecimal.valueOf(161));

        return bmr.multiply(activityFactor);
    }

    @Override
    public BigDecimal goalTypeCalculate(BigDecimal tdee, GoalType goalType) {
        return switch (goalType) {
            case LOSS -> tdee.multiply(BigDecimal.valueOf(0.8));
            case GAIN -> tdee.multiply(BigDecimal.valueOf(1.15));
            case MAINTENANCE -> tdee;
        };
    }

    @Override
    @Transactional
    public String updateUserDetails(UpdateUserDetailsDto userDetailsDto, Long userId) {
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Details about user not found, please fill it in again"
                ));
        if (userDetailsDto.curentWeight() != null) {
            BigDecimal waterNeedsMl = userDetailsDto.curentWeight().multiply(BigDecimal.valueOf(35));
            userModel.setWaterNeeds(waterNeedsMl);
            userModel.setCurrentWeight(userDetailsDto.curentWeight());
        }
        if (userDetailsDto.birthDay() != null) {
            userModel.setBirthDay(userDetailsDto.birthDay());
        }
        if (userDetailsDto.sex() != null) {
            userModel.setSex(userDetailsDto.sex());
        }
        if (userDetailsDto.activityType() != null) {
            userModel.setActivityType(userDetailsDto.activityType());
        }
        if (userDetailsDto.goalType() != null) {
            userModel.setGoalType(userDetailsDto.goalType());
        }
        if (userDetailsDto.wantedWeight() != null) {
            userModel.setWantedWeight(userDetailsDto.wantedWeight());
        }
        if (userDetailsDto.height() != null) {
            userModel.setHeight(userDetailsDto.height());
        }
        if (userModel.getCurrentWeight() != null &&
                userModel.getHeight() != null &&
                userModel.getBirthDay() != null &&
                userModel.getSex() != null &&
                userModel.getActivityType() != null &&
                userModel.getGoalType() != null) {

            int age = Period.between(userModel.getBirthDay(), LocalDate.now()).getYears();

            BigDecimal tdee;
            if (userModel.getSex() == SexType.MALE) {
                tdee = calculateForMale(
                        userModel.getHeight(),
                        userModel.getCurrentWeight(),
                        age,
                        userModel.getActivityType()
                );
            } else {
                tdee = calculateForFemale(
                        userModel.getHeight(),
                        userModel.getCurrentWeight(),
                        age,
                        userModel.getActivityType()
                );
            }

            BigDecimal caloriesTarget = goalTypeCalculate(tdee, userModel.getGoalType());
            userModel.setBmr(caloriesTarget);

            BigDecimal recommendedWeight = calculateRecommendedWeightRange(userModel.getHeight());
            userModel.setRecommendedWeight(recommendedWeight);
        }


        userRepository.save(userModel);
        return "User details updated successfully";
    }
    
    @Override
    public BigDecimal calculateRecommendedWeightRange(BigDecimal heightCm) {
        BigDecimal heightM = heightCm.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal heightSquare = heightM.pow(2);

        BigDecimal avgWeight = BigDecimal.valueOf(21.7).multiply(heightSquare);

        return avgWeight;
    }

    @Override
    @Transactional
    public BigDecimal setNewWeight(BigDecimal weightLogsResponse, Long userId) {

        UserModel user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("No details about user not found"));

        user.setCurrentWeight(weightLogsResponse);

        BigDecimal waterNeedsMl = weightLogsResponse.multiply(BigDecimal.valueOf(35));
        user.setWaterNeeds(waterNeedsMl);

        int age = Period.between(user.getBirthDay(), LocalDate.now()).getYears();

        BigDecimal bmr;
        if(user.getSex()==SexType.MALE){
            bmr = calculateForMale(user.getHeight(), weightLogsResponse, age, user.getActivityType());}
        else {
            bmr = calculateForFemale(user.getHeight(), weightLogsResponse, age, user.getActivityType());
        }
        bmr= goalTypeCalculate(bmr, user.getGoalType());


        user.setBmr(bmr);
        userRepository.save(user);

        WeightLogsEntity weightLog = new WeightLogsEntity(LocalDate.now().minusDays(1),
                user.getHeight(),
                user.getSex(),
                user.getActivityType(),
                user.getCurrentWeight(),
                user.getGoalType(),
                user.getWantedWeight(),
                user.getRecommendedWeight(),
                user.getWaterNeeds(),
                user.getBmr(),
                user);

        weightLogsRepository.save(weightLog);

        WeightMessage message = new WeightMessage();
        message.setUserId(userId);
        message.setWeight(weightLogsResponse);
        message.setSendAt(LocalDate.now());

        return user.getCurrentWeight();
    }

    @Override
    public Boolean detailsExists(Long userId) {

        UserModel userModel = userRepository.findById(userId).orElseGet(()->{
            return null;
        });
        if(userModel == null)
            return false;
        else
            return true;
    }


    @Transactional
    @Override
    public UserInfoResponse getUserInfo(Long userId) {

        UserModel user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("Details about user not found!"));

        UserInfoResponse response = new UserInfoResponse(user.getCurrentWeight(),
                user.getHeight(),
                user.getSex().toString(),
                user.getActivityType().toString(),
                user.getBirthDay(),
                user.getGoalType().toString());


        return response;
    }
}
