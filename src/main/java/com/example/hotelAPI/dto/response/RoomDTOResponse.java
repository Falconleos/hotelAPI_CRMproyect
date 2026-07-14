package com.example.hotelAPI.dto.response;

import com.example.hotelAPI.enums.RoomState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTOResponse {

    private Long id;
    private Integer number;
    private RoomState state;

    private Long roomTypeId;
    private String roomTypeName;
    private Integer capacity;
    private Double pricePerNight;

}
