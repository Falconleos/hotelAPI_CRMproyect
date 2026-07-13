package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.UserDtoRequest;
import com.example.hotelAPI.dto.request.UserLoginDtoRequest;
import com.example.hotelAPI.dto.response.AuthSuccessDtoResponse;
import com.example.hotelAPI.dto.response.AuthTokenResponse;
import com.example.hotelAPI.dto.response.RefreshTokenDtoResponse;
import com.example.hotelAPI.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthTokenResponse>register(@Valid @RequestBody UserDtoRequest request){
        AuthSuccessDtoResponse token = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, token.responseCookie().toString())
                   .body(token.authTokenResponse());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse>login(@Valid @RequestBody UserLoginDtoRequest request){
        AuthSuccessDtoResponse token = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE,token.responseCookie().toString())
                .body(token.authTokenResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenDtoResponse>refresh(
            @CookieValue(name = "refreshToken",required = false)String refreshtoken){
        if(refreshtoken==null || refreshtoken.isBlank()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        RefreshTokenDtoResponse tokenResponse = authService.refreshAccessToken(refreshtoken);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void>logout(){
        ResponseCookie cleanCookie = authService.logout();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,cleanCookie.toString())
                .build();
    }

}
