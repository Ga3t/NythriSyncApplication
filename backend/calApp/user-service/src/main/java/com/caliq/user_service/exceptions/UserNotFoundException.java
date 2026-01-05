package com.caliq.user_service.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String userNotFound) {
    }
}
