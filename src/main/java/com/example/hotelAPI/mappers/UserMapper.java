package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.UserDtoRequest;
import com.example.hotelAPI.dto.response.UserDtoResponse;
import com.example.hotelAPI.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity (UserDtoRequest request);
    UserDtoResponse toDto (UserEntity userEntity);
}
