package com.example.hotelAPI.dto.response;

import com.example.hotelAPI.enums.BookingState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Datos detallados de la reserva en respuesta a consultas")
public class BookingDTOResponse {

    @Schema(description = "ID único de la reserva", example = "1")
    private Long id;

    @Schema(description = "Fecha de ingreso")
    private LocalDate checkIn;

    @Schema(description = "Fecha de egreso")
    private LocalDate checkOut;

    @Schema(description = "Cantidad de pasajeros")
    private Integer guestCount;

    @Schema(description = "Estado actual de la reserva")
    private BookingState state;

    @Schema(description = "Nombre del pasajero titular")
    private String guestFirstName;

    @Schema(description = "Apellido del pasajero titular")
    private String guestLastName;

    @Schema(description = "Teléfono del pasajero titular")
    private String guestPhone;

    @Schema(description = "Observaciones especiales")
    private String observation;

    @Schema(description = "Empleado que gestionó la reserva")
    private EmployeeDTOResponse employee;

    @Schema(description = "Información de la habitación reservada")
    private RoomDTOResponse room;

    @Schema(description = "Precio total final calculado por la estadía")
    private Double totalPrice;

    @Schema(description = "Indica si la reserva se encuentra activa")
    private Boolean active;

}
