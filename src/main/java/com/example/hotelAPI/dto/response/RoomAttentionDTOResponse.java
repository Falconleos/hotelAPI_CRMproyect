package com.example.hotelAPI.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RoomAttentionDTOResponse {
    private Long id;
    private Long checkInId;
    private Long itemId;
    private String itemDescription;
    private Boolean isService;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
    private LocalDateTime createdAt;     // NUEVO
    private String employeeUsername;   // NUEVO (o el nombre del empleado)
}