package com.example.hotelAPI.exceptions;

public class InvalidCheckInException extends RuntimeException {
    public InvalidCheckInException(String message) {
        super(message);
    }
}
