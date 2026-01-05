package com.caliq.user_service.security;


import com.caliq.user_service.exceptions.*;
import com.caliq.user_service.models.RefreshToken;
import com.caliq.user_service.models.UserEntity;
import com.caliq.user_service.repository.RefreshTokenRepository;
import com.caliq.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Component
public class RefreshTokenGenerator {

    public RefreshTokenGenerator(){}

    private static final Duration expTime = Duration.ofDays(30);
    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenGenerator(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public String generateRefreshToken(Long userId){

        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found"));
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setCreatedAt(Instant.now());
        refreshTokenEntity.setExpiryDate(Instant.now().plus(expTime));
        refreshTokenEntity.setUser(user);

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        refreshTokenEntity.setToken(refreshToken);

        refreshTokenRepository.save(refreshTokenEntity);
        return refreshToken;
    }


    @Transactional
    public String refreshAccessToken (String refreshToken){

        RefreshToken refreshTokenEntityOld = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()-> new RefreshTokenNotFoundException("This token incorrect"));
        if(refreshTokenEntityOld.getExpiryDate().isBefore(Instant.now())){
            throw new RefreshTokenExpiredException("The expiration has passed. Please login again");
        }

        UserEntity user = refreshTokenEntityOld.getUser();
        Long userId = user.getUserId();

        if(refreshTokenEntityOld.isRevoked()){
            throw new RefreshTokenRevokedException("The token was revoked. Please login again", userId);
        }

        String tokenNew = generateRefreshToken(userId);
        revokeRefreshToken(refreshTokenEntityOld.getToken(), userId);

        return tokenNew;
    }

    @Transactional
    public void revokeRefreshToken(String refreshToken, Long userId) {

        RefreshToken revokeToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()->new RefreshTokenNotFoundException("No such refresh token"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User not found"));
        if(!user.getUserId().equals(revokeToken.getUser().getUserId())){
            revokeToken.setSpecialNotes("This token was leaked."+ Instant.now().toString());
            refreshTokenRepository.save(revokeToken);
            throw new RefreshTokenLeakedException(revokeToken.getId());
        }

        revokeToken.setRevoked(true);
        refreshTokenRepository.save(revokeToken);
    }


}
