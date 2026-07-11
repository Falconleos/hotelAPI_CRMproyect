package com.example.hotelAPI.repository;

import com.example.hotelAPI.enums.Role;
import com.example.hotelAPI.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Long> {
    Optional<RoleEntity> findByName(Role name);
}
