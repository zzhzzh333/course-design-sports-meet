package com.sportsmeet.common;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}