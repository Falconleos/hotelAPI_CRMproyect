package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.CommentDTORequest;
import com.example.hotelAPI.dto.response.CommentDTOResponse;
import com.example.hotelAPI.model.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "checkIn", ignore = true)
    @Mapping(target = "user", ignore = true)
    CommentEntity toEntity(CommentDTORequest request);

    @Mapping(target = "checkInId", source = "checkIn.id")
    CommentDTOResponse toDto(CommentEntity entity);
}