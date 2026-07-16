package com.example.hotelAPI.exceptions;

public class CheckInNotFoundException extends RuntimeException {
    public CheckInNotFoundException(String message) {
        super(message);
    }
}
