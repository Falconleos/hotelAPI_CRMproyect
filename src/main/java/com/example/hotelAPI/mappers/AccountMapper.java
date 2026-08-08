package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.response.AccountDTOResponse;
import com.example.hotelAPI.dto.response.PaymentDTOResponse;
import com.example.hotelAPI.model.AccountEntity;
import com.example.hotelAPI.model.PaymentEntity;
import com.example.hotelAPI.model.RoomAttentionEntity;
import com.example.hotelAPI.repository.RoomAttentionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final RoomAttentionRepository roomAttentionRepository;

    public AccountDTOResponse toDto(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        // Obtenemos los consumos (RoomAttentions) asociados al Check-In de esta cuenta
        List<AccountDTOResponse.AccountItemDto> items = List.of();
        if (entity.getCheckIn() != null) {
            List<RoomAttentionEntity> attentions = roomAttentionRepository.findByCheckInId(entity.getCheckIn().getId());
            items = attentions.stream().map(att -> AccountDTOResponse.AccountItemDto.builder()
                    .description(att.getItem().getDescription())
                    .quantity(att.getQuantity())
                    .subtotal(att.getSubtotal().doubleValue())
                    .build()).collect(Collectors.toList());
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
                .items(items) // <-- Asignamos la lista de consumos mapeados
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