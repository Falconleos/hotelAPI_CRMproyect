package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.ItemDTORequest;
import com.example.hotelAPI.dto.response.ItemDTOResponse;
import com.example.hotelAPI.model.ItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    ItemEntity toEntity(ItemDTORequest request);

    @Mapping(target = "isService", source = "isService")
    ItemDTOResponse toDto(ItemEntity entity);
}