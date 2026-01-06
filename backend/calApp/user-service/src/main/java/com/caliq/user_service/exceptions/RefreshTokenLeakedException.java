package com.caliq.user_service.exceptions;
public class RefreshTokenLeakedException extends RuntimeException {
    public RefreshTokenLeakedException(Long id) {
    }
}