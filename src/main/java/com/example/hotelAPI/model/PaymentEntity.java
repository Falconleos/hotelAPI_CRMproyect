package com.example.hotelAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Column(nullable = false)
    private Double amount;

    @NotNull(message = "Payment date is required")
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Column(name = "payment_method")
    private String paymentMethod; // Ej: CASH, CREDIT_CARD, TRANSFER

    @Column(name = "transaction_reference")
    private String transactionReference; // Número de comprobante o voucher

    // Ahora un pago puede pertenecer a una cuenta (post-checkin) O a una reserva (seña previa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = true)
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = true)
    private BookingEntity booking;

    // Opcional en PaymentEntity si quieres guardarlo en base de datos:
    private String registeredByName;
    private String registeredBySurname;

}