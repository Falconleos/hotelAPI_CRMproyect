package com.example.hotelAPI.exceptions;

public class UnauthorizedCommentException extends RuntimeException {
    public UnauthorizedCommentException(String message) {
        super(message);
    }
}
