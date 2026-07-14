package com.example.hotelAPI.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTORequest {
    @NotNull(message = "Room number is required")
    @Positive(message = "Room number must be a positive integer")
    private Integer number;

    @NotNull(message = "Room type ID is required")
    private Long roomTypeId;
}
