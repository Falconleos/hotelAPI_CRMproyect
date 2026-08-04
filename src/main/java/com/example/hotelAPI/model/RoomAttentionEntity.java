package com.example.hotelAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_attention")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAttentionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "roomAttentionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEntity> items = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Double adjustment = 0.0; // Descuento (-) o recargo (+) manual

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private Double total = 0.0; // Suma de subtotales + adjustment

    @Builder.Default
    @Column(nullable = false)
    private Boolean paid = false; // Estado de pago del servicio de habitación

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_in_id", nullable = false)
    private CheckInEntity checkIn;

    // Método auxiliar para recalcular el total automáticamente
    public void calculateTotal() {

        this.total = items.stream()
                .map(ItemEntity::getSubtotal)
                .reduce(0.0, Double::sum);

        // Evitar totales negativos por exceso de descuento
        if (total<0.0) {
            total = 0.0;
        }
    }
}