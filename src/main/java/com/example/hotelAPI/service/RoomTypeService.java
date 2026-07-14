package com.example.hotelAPI.service;

import com.example.hotelAPI.model.RoomTypeEntity;

import java.util.List;

public interface RoomTypeService {
    List<RoomTypeEntity> getAllRoomTypes();
    RoomTypeEntity getRoomTypeById(Long id);
    RoomTypeEntity createRoomType(RoomTypeEntity roomType);
    RoomTypeEntity updateRoomType(Long id, RoomTypeEntity roomTypeDetails);
    void deleteRoomType(Long id);
}
