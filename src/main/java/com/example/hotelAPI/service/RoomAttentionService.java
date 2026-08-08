package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.RoomAttentionDTORequest;
import com.example.hotelAPI.dto.response.RoomAttentionDTOResponse;

import java.util.List;

public interface RoomAttentionService {
    RoomAttentionDTOResponse addAttention(RoomAttentionDTORequest request);
    void removeAttention(Long id);
    List<RoomAttentionDTOResponse> getAttentionsByCheckIn(Long checkInId);
}