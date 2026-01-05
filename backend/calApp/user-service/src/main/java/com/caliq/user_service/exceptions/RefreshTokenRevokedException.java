package com.caliq.user_service.exceptions;

public class RefreshTokenRevokedException extends RuntimeException {
    public RefreshTokenRevokedException(String RevokedToken) {
    }
    public RefreshTokenRevokedException(String RevokedToken, Long userId) {}

}
