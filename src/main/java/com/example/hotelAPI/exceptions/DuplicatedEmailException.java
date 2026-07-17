package com.example.hotelAPI.exceptions;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException(String message) {
        super(message);
    }
}
