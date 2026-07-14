package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.EmployeeDTORequest;
import com.example.hotelAPI.dto.response.EmployeeDTOResponse;
import com.example.hotelAPI.enums.Shift;
import com.example.hotelAPI.model.EmployeeEntity;

import java.util.List;

public interface EmployeeService {

    EmployeeEntity findEntityById(Long id);
    EmployeeDTOResponse getById(Long id);
    List<EmployeeDTOResponse> getAll();
    EmployeeDTOResponse createEmployee(EmployeeDTORequest request);
    void deleteEmployee(Long id);
    EmployeeDTOResponse updateEmployee(Long id, EmployeeDTORequest request);
    EmployeeDTOResponse cambiarTurno(Long id, Shift nuevoShift);

}
