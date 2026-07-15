package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.RoomTypeDTORequest;
import com.example.hotelAPI.dto.response.RoomTypeDTOResponse;
import com.example.hotelAPI.model.RoomTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    RoomTypeEntity toEntity(RoomTypeDTORequest request);

    RoomTypeDTOResponse toDto(RoomTypeEntity entity);

}
