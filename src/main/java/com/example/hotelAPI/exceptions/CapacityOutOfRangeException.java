package com.example.hotelAPI.exceptions;

public class CapacityOutOfRangeException extends RuntimeException {
    public CapacityOutOfRangeException(String message) {
        super(message);
    }
}
