package com.example.hotelAPI.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDtoRequest {
    @NotBlank(message = "current password is necesary")
    private String currentPassword;

    @NotBlank(message = "new password is necesary")
    private String newPassword;
}