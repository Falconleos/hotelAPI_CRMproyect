package com.example.hotelAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
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

    @NotNull
    @Column(nullable = false)
    private Double totalAmount;

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private Double paidAmount = 0.0; // Suma acumulada de los pagos

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private Boolean isPaid = false; // Se pone en true cuando paidAmount >= totalAmount

    // Método de negocio para registrar un pago y actualizar saldos
    public void addPayment(PaymentEntity payment) {
        this.payments.add(payment);
        payment.setAccount(this);
        recalculateAccount();
    }

    // Método de negocio para sumar cargos adicionales (como Room Service) y recalcular saldos
    public void addRoomServiceCharge(Double amount) {
        if (amount != null && amount < 0) {
            this.totalAmount += amount;
            recalculateAccount();
        }
    }

    public void recalculateAccount() {
        this.paidAmount = payments.stream()
                .map(PaymentEntity::getAmount)
                .reduce(0.0, Double::sum);

        this.isPaid = this.paidAmount.compareTo(this.totalAmount) >= 0;
    }

    // Método pre-persistencia o inicializador útil antes de guardar en BD
    @PrePersist
    public void prePersist() {
        if (this.totalAmount == null && this.checkIn != null) {
            this.totalAmount = this.checkIn.getTotal();
        }
        recalculateAccount();
    }

}