package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.response.RoomAttentionDTOResponse;
import com.example.hotelAPI.model.RoomAttentionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface RoomAttentionMapper {

    @Mapping(target = "checkInId", source = "checkIn.id")
    RoomAttentionDTOResponse toDto(RoomAttentionEntity entity);

}
