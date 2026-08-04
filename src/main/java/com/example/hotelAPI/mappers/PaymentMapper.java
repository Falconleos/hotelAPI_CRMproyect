package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.PaymentDTORequest;
import com.example.hotelAPI.dto.response.PaymentDTOResponse;
import com.example.hotelAPI.model.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "account", ignore = true)
    PaymentEntity toEntity(PaymentDTORequest request);

    @Mapping(target = "accountId", source = "account.id")
    PaymentDTOResponse toDto(PaymentEntity entity);
}