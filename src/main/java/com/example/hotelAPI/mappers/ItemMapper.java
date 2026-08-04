package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.ItemDTORequest;
import com.example.hotelAPI.dto.response.ItemDTOResponse;
import com.example.hotelAPI.model.ItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subtotal", ignore = true) // Se calcula en el servicio o entidad
    @Mapping(target = "roomService", ignore = true)
    ItemEntity toEntity(ItemDTORequest request);

    ItemDTOResponse toDto(ItemEntity entity);
}