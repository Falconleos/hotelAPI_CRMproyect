package com.example.hotelAPI.dto.request;

import com.example.hotelAPI.model.RoleEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDtoRequest {

    @NotBlank(message = "username is necessary")
    private String username;
    @NotBlank(message = "password is necessary")
    private String password;

    @NotBlank(message = "Name is necessary")
    @Size(min = 3, max = 50, message = "Name must contains between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Surname is necessary")
    @Size(min = 3, max = 50, message = "Surname must contains between 3 and 50 characters")
    private String surname;

    @NotBlank(message = "Dni is necessary")
    @Pattern(
            regexp = "\\d{7,10}",
            message = "Dni must contains between 7 and 10 characters"
    )
    private String dni;

    @NotBlank(message = "Email is necessary")
    @Email(message = "the email must be valid")
    private String email;

    @NotBlank(message = "the phone number is necessary")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "the phone number must contains at least 10 characters"
    )
    private String phoneNumber;

    @Past(message = "birthDay must be past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

}
