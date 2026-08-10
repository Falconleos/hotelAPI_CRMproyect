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

        // Extracción segura de datos del Check-In, Huésped, Habitación y Fechas
        String userName = null;
        String userSurname = null;
        String userDni = null;
        String roomNumber = null;
        String checkInDate = null;
        String checkOutDate = null;

        if (entity.getCheckIn() != null) {
            if (entity.getCheckIn().getBookingEntity() != null) {
                checkInDate = String.valueOf(entity.getCheckIn().getBookingEntity().getCheckIn());
                checkOutDate = String.valueOf(entity.getCheckIn().getBookingEntity().getCheckOut());

                if (entity.getCheckIn().getBookingEntity().getRoom() != null) {
                    roomNumber = String.valueOf(entity.getCheckIn().getBookingEntity().getRoom().getNumber());
                }
            }

            if (entity.getCheckIn().getUserEntity() != null) {
                userName = entity.getCheckIn().getUserEntity().getName();
                userSurname = entity.getCheckIn().getUserEntity().getSurname();
                userDni = entity.getCheckIn().getUserEntity().getDni();
            }
        }

        // Construcción del objeto interno UserDto
        AccountDTOResponse.UserDto userDto = null;
        if (userName != null || userSurname != null || userDni != null) {
            userDto = AccountDTOResponse.UserDto.builder()
                    .name(userName)
                    .surname(userSurname)
                    .dni(userDni)
                    .build();
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

        // Tomamos la estadía base fija desde baseAmount
        double baseAmount = entity.getBaseAmount() != null ? entity.getBaseAmount() : 0.0;

        // Sumamos los subtotales de los servicios
        double itemsTotal = items.stream()
                .mapToDouble(item -> item.getSubtotal() != null ? item.getSubtotal() : 0.0)
                .sum();

        double subtotalGeneral = baseAmount + itemsTotal;

        // Aplicamos el porcentaje de ajuste sobre la suma de (estadia base + servicios)
        int adjustmentPercentage = entity.getAdjustmentPercentage() != null ? entity.getAdjustmentPercentage() : 0;
        double finalTotal = Math.max(0, subtotalGeneral + (subtotalGeneral * adjustmentPercentage / 100.0));

        double paid = entity.getPaidAmount() != null ? entity.getPaidAmount() : 0.0;
        double remaining = Math.max(0, finalTotal - paid);

        return AccountDTOResponse.builder()
                .id(entity.getId())
                .checkInId(entity.getCheckIn() != null ? entity.getCheckIn().getId() : null)
                .baseAmount(baseAmount)          // <-- Enviamos la estadía base fija por separado
                .totalAmount(finalTotal)         // <-- Total general final
                .paidAmount(paid)
                .remainingBalance(remaining)
                .isPaid(entity.getIsPaid())
                .adjustmentPercentage(adjustmentPercentage)
                .user(userDto)
                .roomNumber(roomNumber)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .payments(entity.getPayments() != null ?
                        entity.getPayments().stream().map(this::toPaymentDto).collect(Collectors.toList()) :
                        List.of())
                .items(items)
                .build();
    }

    public PaymentDTOResponse toPaymentDto(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }

        String userName = null;
        String userSurname = null;

        if (entity.getAccount() != null &&
                entity.getAccount().getCheckIn() != null &&
                entity.getAccount().getCheckIn().getUserEntity() != null) {
            userName = entity.getAccount().getCheckIn().getUserEntity().getName();
            userSurname = entity.getAccount().getCheckIn().getUserEntity().getSurname();
        }

        return PaymentDTOResponse.builder()
                .id(entity.getId())
                .accountId(entity.getAccount() != null ? entity.getAccount().getId() : null)
                .amount(entity.getAmount())
                .paymentDate(entity.getPaymentDate())
                .paymentMethod(entity.getPaymentMethod())
                .transactionReference(entity.getTransactionReference())
                .userName(userName)
                .userSurname(userSurname)
                .registeredByName(entity.getRegisteredByName())
                .registeredBySurname(entity.getRegisteredBySurname())
                .build();
    }
}