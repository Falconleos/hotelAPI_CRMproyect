package com.example.hotelAPI.dto.response;

public record RefreshTokenDtoResponse(
        String accessToken,
        String tokenType
) {

    public RefreshTokenDtoResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}
