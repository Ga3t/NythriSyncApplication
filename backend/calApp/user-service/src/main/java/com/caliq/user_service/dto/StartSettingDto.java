package com.caliq.user_service.dto;
import com.caliq.core.enums.ActivityLevel;
import com.caliq.core.enums.SexEnums;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class StartSettingDto {
    Integer age;
    LocalDate birthdayDate;
    BigDecimal growth;
    BigDecimal weight;
    ActivityLevel activityLevel;
    SexEnums sex;
    BigDecimal desiredConsumption;
}