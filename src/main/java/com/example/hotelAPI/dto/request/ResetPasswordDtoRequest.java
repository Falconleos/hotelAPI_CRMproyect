package com.example.hotelAPI.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDtoRequest {
    @NotBlank(message = "the token is necessary")
    private String token;

    @NotBlank(message = "new password is necessary")
    private String newPassword;
}