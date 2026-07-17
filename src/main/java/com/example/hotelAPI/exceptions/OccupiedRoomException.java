package com.example.hotelAPI.exceptions;

public class OccupiedRoomException extends RuntimeException {
    public OccupiedRoomException(String message) {
        super(message);
    }
}
