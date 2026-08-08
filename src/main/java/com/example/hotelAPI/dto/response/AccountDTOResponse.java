package com.example.hotelAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTOResponse {

    private Long id;
    private Long checkInId;
    private Double totalAmount;
    private Double paidAmount;
    private Boolean isPaid;
    private List<PaymentDTOResponse> payments;

    // Agregamos la lista de items/consumos para que Angular los pueda mostrar en la tabla
    private List<AccountItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountItemDto {
        private String description;
        private Integer quantity;
        private Double subtotal;
    }
}