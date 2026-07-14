package com.example.hotelAPI.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Respuesta detallada de la cancelación procesada")
public class BookingCancellationDTOResponse {

    @Schema(description = "Identificador único del registro de cancelación", example = "1")
    private Long id;

    @Schema(description = "Detalles básicos de la reserva que fue cancelada")
    private BookingDTOResponse booking; // Este lo definiremos en el siguiente paso

    @Schema(description = "Motivo de la cancelación", example = "Problemas de salud")
    private String reason;

    @Schema(description = "Fecha y hora exacta en la que se registró la cancelación")
    private LocalDateTime cancellationDate;

}
