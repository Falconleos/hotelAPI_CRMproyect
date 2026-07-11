package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.UserDtoRequest;
import com.example.hotelAPI.dto.request.UserLoginDtoRequest;
import com.example.hotelAPI.dto.response.AuthTokenResponse;
import com.example.hotelAPI.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthTokenResponse>register(@Valid @RequestBody UserDtoRequest request){
        AuthTokenResponse token = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse>login(@Valid @RequestBody UserLoginDtoRequest request){
        AuthTokenResponse token = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }


}
