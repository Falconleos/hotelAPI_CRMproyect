package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.response.AccountDTOResponse;
import com.example.hotelAPI.model.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = {PaymentMapper.class})
public interface AccountMapper {

    @Mapping(target = "checkInId", source = "checkIn.id")
    @Mapping(target = "remainingBalance", source = "entity", qualifiedByName = "calculateRemainingBalance")
    AccountDTOResponse toDto(AccountEntity entity);

    @Named("calculateRemainingBalance")
    default Double calculateRemainingBalance(AccountEntity entity) {
        if (entity.getTotalAmount() == null || entity.getPaidAmount() == null) {
            return 0.0;
        }
        double balance = entity.getTotalAmount()-entity.getPaidAmount();
        return balance < 0 ? 0.0 : balance;
    }
}