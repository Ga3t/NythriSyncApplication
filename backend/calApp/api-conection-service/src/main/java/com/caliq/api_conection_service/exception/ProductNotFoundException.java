package com.caliq.api_conection_service.exception;
public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String s) {
        super(s);
    }
}