package com.example.hotelAPI.repository;

import com.example.hotelAPI.model.RoomAttentionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomAttentionRepository extends JpaRepository<RoomAttentionEntity, Long> {
    List<RoomAttentionEntity> findByCheckInId(Long checkInId);
}