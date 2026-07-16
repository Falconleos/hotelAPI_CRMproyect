package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.UserDtoRequest;
import com.example.hotelAPI.dto.request.UserDtoRequestCreation;
import com.example.hotelAPI.dto.response.UserDtoResponse;
import com.example.hotelAPI.enums.Role;
import com.example.hotelAPI.exceptions.DuplicatedDNIException;
import com.example.hotelAPI.exceptions.DuplicatedEmailException;
import com.example.hotelAPI.exceptions.InvalidNameException;
import com.example.hotelAPI.exceptions.UserNotFoundException;
import com.example.hotelAPI.mappers.UserMapper;
import com.example.hotelAPI.model.RoleEntity;
import com.example.hotelAPI.model.UserEntity;
import com.example.hotelAPI.repository.RoleRepository;
import com.example.hotelAPI.repository.UserRepository;
import com.example.hotelAPI.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDtoResponse getById(Long id) {
        return userMapper.toDto(findEntityById(id));
    }

    @Override
    public UserEntity findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow( ()->new UsernameNotFoundException("user not found") );
    }

    @Override
    public List<UserDtoResponse> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    //preauthorize admin - recepcionist
    // Implementación en UserServiceImpl.java
    @Override
    public UserDtoResponse createUserWithRole(UserDtoRequestCreation request) {
        // 1. Validaciones de existencia
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicatedEmailException("email already exists");
        }
        if (userRepository.existsByDni(request.getDni())) {
            throw new DuplicatedDNIException("dni already exists");
        }

        // 2. Mapear DTO a Entidad
        UserEntity userEntity = userMapper.toEntity(request);

        // 3. Obtener el rol dinámico enviado en el request
        RoleEntity userRole = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new InvalidNameException("No role named: " + request.getRole()));
        userEntity.setRoles(Set.of(userRole));

        // 4. Encriptar contraseña provista
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));

        // 5. Configurar estados de seguridad de la cuenta
        userEntity.setCreateAt(LocalDate.now());
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setEnabled(true);

        // 6. Guardar en Base de Datos
        UserEntity savedUser = userRepository.save(userEntity);

        return userMapper.toDto(savedUser);
    }

    //preauthorize admin
    @Override
    public void deleteUser(Long id) {
        UserEntity userEntity = findEntityById(id);
        userRepository.delete(userEntity);
    }

    //preauthorize admin-recepcionist-guest
    @Override
    @Transactional
    public UserDtoResponse updateUser(Long id, UserDtoRequest userDtoRequest) {
        UserEntity userEntity = findEntityById(id);

        // 2. Validar DNI: si el DNI ingresado es diferente al actual, verificar que no exista en otro usuario
        if (!userEntity.getDni().equals(userDtoRequest.getDni()) &&
                userRepository.existsByDni(userDtoRequest.getDni())) {
            throw new DuplicatedDNIException("Dni already exists");
        }

        // 3. Validar Email: si el Email ingresado es diferente al actual, verificar duplicados
        if (!userEntity.getEmail().equals(userDtoRequest.getEmail()) &&
                userRepository.existsByEmail(userDtoRequest.getEmail())) {
            throw new DuplicatedEmailException("Email already exists");
        }

        // 4. Modificaciones (Agregamos el DNI aquí para que efectivamente se actualice si cambió)
        userEntity.setName(userDtoRequest.getName());
        userEntity.setSurname(userDtoRequest.getSurname());
        userEntity.setDni(userDtoRequest.getDni());
        userEntity.setEmail(userDtoRequest.getEmail());
        userEntity.setPhoneNumber(userDtoRequest.getPhoneNumber());

        return userMapper.toDto(userEntity);
    }

    @Override
    public UserDtoResponse userByDni(String dni) {
        UserEntity userEntity = userRepository.findByDni(dni)
                .orElseThrow( ()->new UserNotFoundException("user does not exist with dni: " + dni) );
        return userMapper.toDto(userEntity);
    }

    @Override
    public UserDtoResponse findByUsername(String username) {
        return userMapper.toDto(userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("username inexistent")));
    }
}
