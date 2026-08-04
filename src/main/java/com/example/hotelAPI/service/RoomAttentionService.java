package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.RoomAttentionDTORequest;
import com.example.hotelAPI.dto.response.RoomAttentionDTOResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
public interface RoomAttentionService {
    RoomAttentionDTOResponse createRoomAttention(RoomAttentionDTORequest request);
    List<RoomAttentionDTOResponse> getRoomAttentionByCheckIn(Long checkInId);
    RoomAttentionDTOResponse payRoomAttention(Long id);
    void deleteRoomAttention(Long id);
}
