package com.example.hotelAPI.repository;

import com.example.hotelAPI.dto.response.RoomDTOResponse;
import com.example.hotelAPI.enums.RoomState;
import com.example.hotelAPI.model.RoomEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity,Long> {
    Optional<Object> findByNumber(@NotNull(message = "Room number is required") @Positive(message = "Room number must be a positive integer") Integer number);
    List<RoomEntity> findByState(RoomState roomState);
}

