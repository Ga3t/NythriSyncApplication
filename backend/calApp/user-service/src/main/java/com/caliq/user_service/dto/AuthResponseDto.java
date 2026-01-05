package com.caliq.user_service.dto;

import lombok.Data;

@Data
public class AuthResponseDto {

    String jwtToken;
    String refreshToken;


    public AuthResponseDto(String jwtToken, String refreshToken) {
        this.jwtToken = jwtToken;
        this.refreshToken= refreshToken;
    }
}
