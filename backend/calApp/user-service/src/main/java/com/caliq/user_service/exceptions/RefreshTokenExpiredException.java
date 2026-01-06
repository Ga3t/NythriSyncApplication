package com.caliq.user_service.exceptions;
public class RefreshTokenExpiredException extends RuntimeException{
    public RefreshTokenExpiredException(String TokenExpired) {
    }
}