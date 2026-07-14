package com.example.hotelAPI.dto.response;

import com.example.hotelAPI.enums.Shift;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmployeeDTOResponse {
    private Long id; // Este ID coincide exactamente con el del usuario debido a @MapsId

    private UserDtoResponse user; // Trae name, surname, email, dni, etc.

    private Shift shift;

    private Double salary;
}
