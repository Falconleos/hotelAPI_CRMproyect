package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.response.AccountDTOResponse;
import com.example.hotelAPI.dto.response.PaymentDTOResponse;
import com.example.hotelAPI.model.AccountEntity;
import com.example.hotelAPI.model.PaymentEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {

    public AccountDTOResponse toDto(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        return AccountDTOResponse.builder()
                .id(entity.getId())
                .checkInId(entity.getCheckIn() != null ? entity.getCheckIn().getId() : null)
                .totalAmount(entity.getTotalAmount())
                .paidAmount(entity.getPaidAmount())
                .isPaid(entity.getIsPaid())
                .payments(entity.getPayments() != null ?
                        entity.getPayments().stream().map(this::toPaymentDto).collect(Collectors.toList()) :
                        List.of())
                .build();
    }

    public PaymentDTOResponse toPaymentDto(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }

        return PaymentDTOResponse.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .paymentDate(entity.getPaymentDate())
                .paymentMethod(entity.getPaymentMethod())
                .transactionReference(entity.getTransactionReference())
                .build();
    }
}