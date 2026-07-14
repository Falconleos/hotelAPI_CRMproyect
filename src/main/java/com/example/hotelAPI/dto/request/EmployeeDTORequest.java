package com.example.hotelAPI.dto.request;

import com.example.hotelAPI.enums.Shift;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmployeeDTORequest {
    @NotNull(message = "User ID is required to link the employee profile")
    private Long userId;

    @NotNull(message = "Shift is required")
    private Shift shift;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be a positive number greater than zero")
    private Double salary;
}
