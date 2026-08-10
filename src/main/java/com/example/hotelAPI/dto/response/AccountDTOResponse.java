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

    private Double baseAmount;       // <-- Valor fijo de la estadía base
    private Double totalAmount;      // <-- Total general final (Base + ítems + porcentaje de ajuste)
    private Double paidAmount;
    private Double remainingBalance;
    private Boolean isPaid;

    // Atributo para el porcentaje de ajuste (+/-)
    private Integer adjustmentPercentage;

    // Campos para la vista general en Angular
    private UserDto user;
    private String roomNumber;
    private String checkInDate;
    private String checkOutDate;

    private List<PaymentDTOResponse> payments;
    private List<AccountItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private String name;
        private String surname;
        private String dni;
    }

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