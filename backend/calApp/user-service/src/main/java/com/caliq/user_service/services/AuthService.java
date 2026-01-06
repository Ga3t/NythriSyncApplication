package com.caliq.user_service.services;
import com.caliq.user_service.dto.AuthResponseDto;
import com.caliq.user_service.dto.LoginDto;
import com.caliq.user_service.dto.RegistrationDto;
import org.springframework.stereotype.Service;
@Service
public interface AuthService {
    AuthResponseDto authenticateUser (LoginDto loginDto);
    String registrateUser(RegistrationDto registrationDto);
    AuthResponseDto refreshToken(String refreshToken);
}