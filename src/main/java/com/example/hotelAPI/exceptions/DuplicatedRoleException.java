package com.example.hotelAPI.exceptions;

public class DuplicatedRoleException extends RuntimeException {
    public DuplicatedRoleException(String message) {
        super(message);
    }
}
