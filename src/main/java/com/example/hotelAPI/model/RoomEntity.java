package com.example.hotelAPI.model;

import com.example.hotelAPI.enums.RoomState;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "rooms")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomTypeEntity type;

}