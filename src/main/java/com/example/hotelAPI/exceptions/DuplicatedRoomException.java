package com.example.hotelAPI.exceptions;

public class DuplicatedRoomException extends RuntimeException {
    public DuplicatedRoomException(String message) {
        super(message);
    }
}
