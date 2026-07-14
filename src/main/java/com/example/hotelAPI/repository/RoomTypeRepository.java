package com.example.hotelAPI.repository;

import com.example.hotelAPI.model.RoomTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomTypeEntity,Long> {
    Optional<RoomTypeEntity> findByName(String name);
    boolean existsByName(String name);
}
