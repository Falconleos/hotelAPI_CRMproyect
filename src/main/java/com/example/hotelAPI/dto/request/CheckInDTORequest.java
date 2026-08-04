package com.example.hotelAPI.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Datos requeridos para registrar el ingreso (Check-In) de un huésped")
public class CheckInDTORequest {

    @NotNull(message = "Booking ID is required")
    @Schema(description = "Identificador de la reserva asociada", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bookingId;

    @NotNull(message = "User ID is required")
    @Schema(description = "Identificador del huésped (usuario)", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull(message = "Total price is required")
    @Positive(message = "Total must be a positive number greater than zero")
    @Schema(description = "Monto total a cobrar por la estadía", example = "350.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double total;

}
