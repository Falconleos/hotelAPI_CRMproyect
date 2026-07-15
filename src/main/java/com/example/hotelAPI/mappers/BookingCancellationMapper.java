package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.response.BookingCancellationDTOResponse;
import com.example.hotelAPI.model.BookingCancellationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BookingMapper.class, EmployeeMapper.class})
public interface BookingCancellationMapper {
    @Mapping(source = "booking", target = "booking")
    @Mapping(source = "employee", target = "employee")
    @Mapping(source = "cancellationDate", target = "cancellationDate")
    @Mapping(source = "reason", target = "reason")
    BookingCancellationDTOResponse toDto(BookingCancellationEntity entity);
}
