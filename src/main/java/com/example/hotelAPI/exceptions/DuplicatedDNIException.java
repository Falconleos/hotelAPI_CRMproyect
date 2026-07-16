package com.example.hotelAPI.exceptions;

public class DuplicatedDNIException extends RuntimeException {
    public DuplicatedDNIException(String message) {
        super(message);
    }
}
