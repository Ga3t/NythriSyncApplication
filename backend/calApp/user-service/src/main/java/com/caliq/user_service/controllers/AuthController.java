package com.caliq.user_service.controllers;


import com.caliq.user_service.dto.AuthResponseDto;
import com.caliq.user_service.dto.LoginDto;
import com.caliq.user_service.dto.RegistrationDto;
import com.caliq.user_service.services.AuthService;
import com.caliq.user_service.services.impl.AuthServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        AuthResponseDto authResponseDto = authService.authenticateUser(loginDto);
        
        Cookie refreshTokenCookie = new Cookie("refresh_token", authResponseDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); 
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); 
        response.addCookie(refreshTokenCookie);
        
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    @PostMapping("registration")
    public ResponseEntity<String> registration(@RequestBody RegistrationDto registrationDto){
        String response = authService.registrateUser(registrationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("refreshtoken")
    public ResponseEntity<AuthResponseDto> refreshtoken(
            @CookieValue(value = "refresh_token", required = false) String refreshTokenCookie,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshTokenHeader,
            HttpServletResponse response) {
        
    
        String refreshToken = refreshTokenCookie != null ? refreshTokenCookie : refreshTokenHeader;
        
        if (refreshToken == null || refreshToken.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        AuthResponseDto authResponseDto = authService.refreshToken(refreshToken);
        
        Cookie newRefreshTokenCookie = new Cookie("refresh_token", authResponseDto.getRefreshToken());
        newRefreshTokenCookie.setHttpOnly(true);
        newRefreshTokenCookie.setSecure(true); 
        newRefreshTokenCookie.setPath("/");
        newRefreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); 
        response.addCookie(newRefreshTokenCookie);
        
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }
}
