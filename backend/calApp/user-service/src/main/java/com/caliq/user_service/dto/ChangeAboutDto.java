package com.caliq.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ChangeAboutDto {

    String email;
    String passwordOld;
    String passwordNew;
    String username;
    LocalDateTime birthdayDate;
}
