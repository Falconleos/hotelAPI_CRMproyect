package com.example.hotelAPI.repository;

import com.example.hotelAPI.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity>findByUsername(String username);
    Optional<UserEntity>findByEmail(String email);

    Optional<UserEntity>findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);

}
