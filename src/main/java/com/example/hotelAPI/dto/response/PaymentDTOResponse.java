package com.example.hotelAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTOResponse {
    private Long id;
    private Long accountId;
    private Long bookingId;
    private Double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String transactionReference;
    private String userName;        // Huésped de la cuenta/check-in
    private String userSurname;     // Huésped de la cuenta/check-in
    private String registeredByName;    // Nombre del usuario logueado
    private String registeredBySurname; // Apellido del usuario logueado
}