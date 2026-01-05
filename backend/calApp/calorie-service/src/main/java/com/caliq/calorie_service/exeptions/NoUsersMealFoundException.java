package com.caliq.calorie_service.exeptions;

public class NoUsersMealFoundException extends RuntimeException {
    public NoUsersMealFoundException(String message) {
        super(message);
    }
}
