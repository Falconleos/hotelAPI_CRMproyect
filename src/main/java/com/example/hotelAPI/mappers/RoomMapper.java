package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.RoomDTORequest;
import com.example.hotelAPI.dto.request.RoomUpdateDTO;
import com.example.hotelAPI.dto.response.RoomDTOResponse;
import com.example.hotelAPI.model.RoomEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    // 1. De Entidad a DTO de Respuesta (Aplanamiento de datos)
    @Mappings({
            @Mapping(source = "type.id", target = "roomTypeId"),
            @Mapping(source = "type.name", target = "roomTypeName"),
            @Mapping(source = "type.capacity", target = "capacity"),
            @Mapping(source = "type.pricePerNight", target = "pricePerNight")
    })
    RoomDTOResponse toResponse(RoomEntity entity);

    // 2. De DTO de Creación a Entidad
    // Ignoramos "id", "state" y "type" porque los manejamos en la lógica del ServiceImpl
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "state", ignore = true),
            @Mapping(target = "type", ignore = true)
    })
    RoomEntity toEntity(RoomDTORequest dto);

    // 3. Actualizar una Entidad existente desde el DTO de actualización
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "state", ignore = true),
            // Ignoramos "type" en el mapeador directo porque el Service busca el RoomTypeEntity
            // de la base de datos por ID y lo asigna de forma segura.
            @Mapping(target = "type", ignore = true)
    })
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRoomFromDto(RoomUpdateDTO dto, @MappingTarget RoomEntity entity);

}
