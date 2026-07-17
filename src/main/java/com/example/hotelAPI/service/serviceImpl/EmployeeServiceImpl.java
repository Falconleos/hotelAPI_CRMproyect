package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.EmployeeDTORequest;
import com.example.hotelAPI.dto.response.EmployeeDTOResponse;
import com.example.hotelAPI.enums.Shift;
import com.example.hotelAPI.exceptions.DuplicatedUserException;
import com.example.hotelAPI.exceptions.UserNotFoundException;
import com.example.hotelAPI.mappers.EmployeeMapper;
import com.example.hotelAPI.model.EmployeeEntity;
import com.example.hotelAPI.model.UserEntity;
import com.example.hotelAPI.repository.EmployeeRepository;
import com.example.hotelAPI.service.EmployeeService;
import com.example.hotelAPI.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserService userService; // Usamos composición inyectando el servicio de usuarios
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeEntity findEntityById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Empleado no encontrado con ID: " + id));
    }

    @Override
    public EmployeeDTOResponse getById(Long id) {
        return employeeMapper.toDto(findEntityById(id));
    }

    @Override
    public List<EmployeeDTOResponse> getAll() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EmployeeDTOResponse createEmployee(EmployeeDTORequest request) {
        // 3.1. Validar que no exista ya un perfil de empleado para ese ID de usuario
        if (employeeRepository.existsById(request.getUserId())) {
            throw new DuplicatedUserException("El usuario ya tiene un perfil de empleado registrado.");
        }

        // 3.2. Recuperar la entidad User usando el UserService que ya creaste
        // (Asumimos que el User se creó previamente en un paso 1 con rol RECEPCIONIST, ADMIN, etc.)
        UserEntity userEntity = userService.findEntityById(request.getUserId());

        // 3.3. Construir la entidad de Empleado asignándole el usuario (Composición)
        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .user(userEntity)
                .shift(request.getShift())
                .salary(request.getSalary())
                .build();

        // 3.4. Guardar en base de datos y retornar el DTO
        return employeeMapper.toDto(employeeRepository.save(employeeEntity));
    }

    @Override
    public void deleteEmployee(Long id) {
        EmployeeEntity employee = findEntityById(id);
        employeeRepository.delete(employee);
    }

    @Override
    public EmployeeDTOResponse updateEmployee(Long id, EmployeeDTORequest request) {
        EmployeeEntity employee = findEntityById(id);

        // Modificamos datos específicos del empleado
        employee.setShift(request.getShift());
        employee.setSalary(request.getSalary());

        // Si necesitas actualizar datos del usuario al mismo tiempo, delegamos en el servicio de usuarios:
        // userService.updateUser(employee.getUser().getId(), request.getUserDtoRequest());

        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional
    public EmployeeDTOResponse cambiarTurno(Long id, Shift nuevoShift) {
        EmployeeEntity employee = findEntityById(id);
        if (employee.getShift().equals(nuevoShift)) {
            throw new IllegalArgumentException("El empleado ya se encuentra asignado a ese turno");
        }
        employee.setShift(nuevoShift);
        return employeeMapper.toDto(employee);
    }
}
