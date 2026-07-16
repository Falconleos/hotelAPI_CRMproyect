package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.CheckInDTORequest;
import com.example.hotelAPI.dto.response.CheckInDTOResponse;
import com.example.hotelAPI.model.CheckInEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {BookingMapper.class, UserMapper.class, EmployeeMapper.class}
)
public interface CheckInMapper {

    // 1. De DTO de Creación (Request) a Entidad física
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checkInState", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "bookingEntity", ignore = true) // Resuelto en Service
    @Mapping(target = "userEntity", ignore = true)       // Resuelto en Service
    @Mapping(target = "employeeEntity", ignore = true)   // Resuelto en Service
    CheckInEntity toEntity(CheckInDTORequest request);

    // 2. De Entidad a DTO de Respuesta (Response)
    @Mapping(source = "bookingEntity", target = "booking")
    @Mapping(source = "userEntity", target = "user")
    @Mapping(source = "employeeEntity", target = "employee")
    CheckInDTOResponse toDto(CheckInEntity entity);

}
