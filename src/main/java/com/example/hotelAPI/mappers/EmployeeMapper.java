package com.example.hotelAPI.mappers;

import com.example.hotelAPI.dto.request.EmployeeDTORequest;
import com.example.hotelAPI.dto.response.EmployeeDTOResponse;
import com.example.hotelAPI.model.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface EmployeeMapper {

    EmployeeDTOResponse toDto(EmployeeEntity employeeEntity);
    @Mapping(target = "id", source = "userId")
    @Mapping(target = "user", ignore = true)
    EmployeeEntity toEntity(EmployeeDTORequest employeeDTORequest);
}
