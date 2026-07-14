package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.UserDtoRequest;
import com.example.hotelAPI.dto.response.UserDtoResponse;
import com.example.hotelAPI.model.UserEntity;

import java.util.List;

public interface UserService {
    UserDtoResponse getById (Long id);
    UserEntity findEntityById(Long id);
    List<UserDtoResponse> getAll();
    UserDtoResponse createUser(UserDtoRequest userDtoRequest);
    void deleteUser (Long id);
    UserDtoResponse updateUser(Long id, UserDtoRequest userDtoRequest);
    UserDtoResponse userByDni(String dni);
}
