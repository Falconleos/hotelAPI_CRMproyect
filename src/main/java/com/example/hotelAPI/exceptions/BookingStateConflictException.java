package com.example.hotelAPI.exceptions;

public class BookingStateConflictException extends RuntimeException {
    public BookingStateConflictException(String message) {
        super(message);
    }
}
