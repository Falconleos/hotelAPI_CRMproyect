package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.CheckInDTORequest;
import com.example.hotelAPI.dto.response.CheckInDTOResponse;
import com.example.hotelAPI.model.AccountEntity;
import com.example.hotelAPI.model.CheckInEntity;
import com.example.hotelAPI.repository.AccountRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        uses = {BookingMapper.class, UserMapper.class, EmployeeMapper.class}
)
public abstract class CheckInMapper {

    @Autowired
    protected AccountRepository accountRepository;

    // 1. De DTO de Creación (Request) a Entidad física
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checkInState", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "bookingEntity", ignore = true)
    @Mapping(target = "userEntity", ignore = true)
    @Mapping(target = "employeeEntity", ignore = true)
    public abstract CheckInEntity toEntity(CheckInDTORequest request);

    // 2. De Entidad a DTO de Respuesta (Response)
    @Mapping(source = "bookingEntity", target = "booking")
    @Mapping(source = "userEntity", target = "user")
    @Mapping(source = "employeeEntity", target = "employee")
    @Mapping(target = "total", expression = "java(getTotalAmount(entity))")
    @Mapping(target = "paid", expression = "java(getIsPaid(entity))")
    public abstract CheckInDTOResponse toDto(CheckInEntity entity);

    // Método auxiliar para mantener el precio base original del Check-In intacto
    protected Double getTotalAmount(CheckInEntity entity) {
        if (entity == null) return 0.0;
        return entity.getTotal() != null ? entity.getTotal() : 0.0;
    }

    protected Boolean getIsPaid(CheckInEntity entity) {
        if (entity == null || entity.getId() == null) return false;
        return accountRepository.findByCheckInId(entity.getId())
                .map(AccountEntity::getIsPaid)
                .orElse(false);
    }
}