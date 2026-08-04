package com.example.hotelAPI.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTOResponse {
    private Long id;
    private Double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String transactionReference;
    private Long accountId;
}