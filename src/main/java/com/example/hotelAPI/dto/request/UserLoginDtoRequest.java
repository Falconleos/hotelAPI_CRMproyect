package com.example.hotelAPI.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserLoginDtoRequest {

    @NotBlank(message = "username is necessary")
    private String username;
    @NotBlank(message = "password is necessary")
    private String password;

}
