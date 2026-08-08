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

}
