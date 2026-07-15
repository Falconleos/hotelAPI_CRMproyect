package com.example.hotelAPI.model;

import com.example.hotelAPI.enums.BookingState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @Column(nullable = false)
    private Integer guestCount; // Cantidad de pasajeros (pax)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingState state;

    @Column(nullable = false, length = 100)
    private String guestFirstName;

    @Column(nullable = false, length = 100)
    private String guestLastName;

    @Column(nullable = false, length = 30)
    private String guestPhone;

    @Column(length = 250)
    private String observation;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @Column(nullable = false)
    private Double totalPrice;

    // Relación uno a uno con la cancelación (La crearemos en el paso siguiente)
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BookingCancellationEntity cancellation;

    private LocalDateTime createdAt;

}
