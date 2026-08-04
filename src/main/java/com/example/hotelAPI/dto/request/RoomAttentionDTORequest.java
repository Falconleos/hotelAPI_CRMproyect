package com.example.hotelAPI.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAttentionDTORequest {
    @NotNull(message = "Check-in ID is required")
    private Long checkInId;

    @NotEmpty(message = "Room service must contain at least one item")
    private List<ItemDTORequest> items;

    private Double adjustment; // Opcional (descuento o recargo manual)
}
