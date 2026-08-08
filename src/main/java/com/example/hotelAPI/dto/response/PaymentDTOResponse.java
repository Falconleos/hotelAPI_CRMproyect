package com.example.hotelAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTOResponse {
    private Long id;
    private Long accountId;
    private Double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String transactionReference;
}