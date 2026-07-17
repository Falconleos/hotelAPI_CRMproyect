package com.example.hotelAPI.exceptions;

public class DisabledRoomException extends RuntimeException {
    public DisabledRoomException(String message) {
        super(message);
    }
}
