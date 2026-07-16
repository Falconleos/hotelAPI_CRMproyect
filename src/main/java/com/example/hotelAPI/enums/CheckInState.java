package com.example.hotelAPI.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipos de estado de la ESTADIA en el sistema")
public enum CheckInState {

    @Schema(description = "Estadia en curso")
    CURRENTLY_ACTIVE,

    @Schema(description = "Estadia completada")
    COMPLETED,

    @Schema(description = "Estadia interrumpida")
    INTERRUPTED;
}
