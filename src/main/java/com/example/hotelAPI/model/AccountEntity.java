package com.example.hotelAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_in_id", nullable = false, unique = true)
    private CheckInEntity checkIn;

    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentEntity> payments = new ArrayList<>();

    // 1. Estadía base fija (Valor original del check-in, no debe alterarse)
    @NotNull
    @Column(nullable = false)
    private Double baseAmount;

    // 2. Subtotal aislado para consumos/servicios extra (Room Attention, ítems, etc.)
    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private Double servicesTotal = 0.0;

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private Double paidAmount = 0.0; // Suma acumulada de los pagos

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private Boolean isPaid = false; // Se pone en true cuando paidAmount >= total final

    // Porcentaje de recargo (+) o descuento (-)
    @Builder.Default
    @Column(nullable = false)
    private Integer adjustmentPercentage = 0;

    // Método de negocio para registrar un pago y actualizar saldos
    public void addPayment(PaymentEntity payment) {
        this.payments.add(payment);
        payment.setAccount(this);
        recalculateAccount();
    }

    public void recalculateAccount() {
        this.paidAmount = payments.stream()
                .map(PaymentEntity::getAmount)
                .reduce(0.0, Double::sum);

        this.isPaid = this.paidAmount.compareTo(this.getFinalTotal()) >= 0;
    }

    // Método que suma los subtotales pero los mantiene separados en la base de datos
    public Double getSubtotal() {
        double base = this.baseAmount != null ? this.baseAmount : 0.0;
        double extras = this.servicesTotal != null ? this.servicesTotal : 0.0;
        return base + extras;
    }

    // Método para obtener el total final considerando la estadía base + servicios + ajuste por porcentaje
    public Double getFinalTotal() {
        double subtotal = this.getSubtotal();
        int percentage = this.adjustmentPercentage != null ? this.adjustmentPercentage : 0;
        double adjusted = subtotal + (subtotal * percentage / 100.0);
        return Math.max(0.0, adjusted);
    }

    @PrePersist
    public void prePersist() {
        if (this.baseAmount == null && this.checkIn != null) {
            this.baseAmount = this.checkIn.getTotal();
        }
        if (this.servicesTotal == null) {
            this.servicesTotal = 0.0;
        }
        recalculateAccount();
    }
}