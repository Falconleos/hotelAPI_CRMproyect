package com.example.hotelAPI.dto.response;

import com.example.hotelAPI.enums.CheckInState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Datos detallados de un registro de Check-In")
public class CheckInDTOResponse {

    @Schema(description = "ID único del registro de Check-In", example = "1")
    private Long id;

    @Schema(description = "Información detallada de la reserva vinculada")
    private BookingDTOResponse booking;

    @Schema(description = "Información detallada del huésped")
    private UserDtoResponse user;

    @Schema(description = "Información detallada del empleado que registró el ingreso")
    private EmployeeDTOResponse employee;

    @Schema(description = "Estado actual de la estadía (CURRENT, COMPLETED, INTERRUPTED)", example = "CURRENT")
    private CheckInState checkInState;

    @Schema(description = "Costo total asignado al ingreso", example = "350000.00")
    private Double total;

    @Schema(description = "Indica si el check-in se encuentra pago", example = "true")
    private Boolean paid;

    @Schema(description = "Indica si el registro se encuentra activo administrativamente", example = "true")
    private Boolean active;

}
