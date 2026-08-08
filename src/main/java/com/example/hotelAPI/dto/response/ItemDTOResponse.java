package com.example.hotelAPI.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTOResponse {
    private Long id;
    private String description;
    private Integer quantity;
    private Double unitPrice;
    private Boolean isService; // Nuevo
}