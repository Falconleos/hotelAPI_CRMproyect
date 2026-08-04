package com.example.hotelAPI.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAttentionDTOResponse {

    private Long id;
    private Long checkInId;
    private List<ItemDTOResponse> items;
    private Double adjustment;
    private Double total;
    private Boolean paid;

}
