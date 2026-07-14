package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.BookingDTORequest;
import com.example.hotelAPI.dto.response.BookingDTOResponse;
import com.example.hotelAPI.model.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoomMapper.class, EmployeeMapper.class})
public interface BookingMapper {

    // Al crear una reserva desde el DTORequest, ignoramos campos que se setean a mano en el Service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "cancellation", ignore = true)
    // Mapeamos los campos del pasajero titular
    @Mapping(source = "guestFirstName", target = "guestFirstName")
    @Mapping(source = "guestLastName", target = "guestLastName")
    @Mapping(source = "guestPhone", target = "guestPhone")
    @Mapping(source = "observation", target = "observation")
    BookingEntity toEntity(BookingDTORequest request);

    // Mapeamos de Entidad a DTOResponse
    @Mapping(source = "employee", target = "employee")
    @Mapping(source = "room", target = "room")
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(source = "active", target = "active")
    BookingDTOResponse toDto(BookingEntity entity);

}
