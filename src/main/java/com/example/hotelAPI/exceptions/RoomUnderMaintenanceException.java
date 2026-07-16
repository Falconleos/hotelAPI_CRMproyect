package com.example.hotelAPI.exceptions;

public class RoomUnderMaintenanceException extends RuntimeException {
    public RoomUnderMaintenanceException(String message) {
        super(message);
    }
}
