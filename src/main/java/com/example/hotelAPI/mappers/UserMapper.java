package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.UserDtoRequest;
import com.example.hotelAPI.dto.request.UserDtoRequestCreation;
import com.example.hotelAPI.dto.response.UserDtoResponse;
import com.example.hotelAPI.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "employee", ignore = true)
    UserEntity toEntity (UserDtoRequest request);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "employee", ignore = true)
    UserEntity toEntity (UserDtoRequestCreation request);

    // Mapeos explícitos para resolver el choque MapStruct <-> Lombok en booleanos
    @Mapping(source = "accountNonExpired", target = "accountNonExpired")
    @Mapping(source = "accountNonLocked", target = "accountNonLocked")
    @Mapping(source = "credentialsNonExpired", target = "credentialsNonExpired")
    @Mapping(source = "enabled", target = "enabled")
    UserDtoResponse toDto (UserEntity userEntity);
}
