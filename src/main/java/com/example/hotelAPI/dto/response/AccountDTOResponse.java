package com.example.hotelAPI.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTOResponse {
    private Long id;
    private Long checkInId;
    private Double totalAmount;
    private Double paidAmount;
    private Double remainingBalance; // totalAmount - paidAmount
    private Boolean isPaid;
    private List<PaymentDTOResponse> payments;
}