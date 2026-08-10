package com.example.hotelAPI.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTORequest {

    // Ya no es estrictamente obligatorio, puede ser null si el pago es una seña para una reserva
    private Long accountId;

    // Nuevo campo opcional para asociar el pago directamente a una reserva (seña)
    private Long bookingId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    private Double amount;

    private String paymentMethod; // Ej: CASH, CREDIT_CARD, TRANSFER

    private String transactionReference; // Número de comprobante o voucher
}