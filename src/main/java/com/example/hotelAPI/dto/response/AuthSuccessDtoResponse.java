package com.example.hotelAPI.dto.response;

import org.springframework.http.ResponseCookie;

public record AuthSuccessDtoResponse(
        AuthTokenResponse authTokenResponse,
        ResponseCookie responseCookie
) {
}
