package com.example.hotelAPI.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomTypeDTOResponse {

    private Long id;
    private String name;
    private Integer capacity;
    private String description;
    private Double pricePerNight;

}
