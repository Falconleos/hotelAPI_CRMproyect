package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.BookingCancellationDTORequest;
import com.example.hotelAPI.dto.request.BookingDTORequest;
import com.example.hotelAPI.dto.response.BookingCancellationDTOResponse;
import com.example.hotelAPI.dto.response.BookingDTOResponse;
import com.example.hotelAPI.dto.response.RoomDTOResponse;
import com.example.hotelAPI.model.BookingEntity;
import com.example.hotelAPI.model.RoomEntity;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingEntity findEntityById(Long id);

    BookingDTOResponse findById(Long id);

    List<BookingDTOResponse> getBookings(Boolean active);

    BookingDTOResponse createBooking(BookingDTORequest request);

    BookingCancellationDTOResponse cancelBooking(BookingCancellationDTORequest request);

    void update(BookingEntity booking);

    BookingDTOResponse confirmBooking(Long id);

    List<RoomEntity> getAvailableRoomsEntities(LocalDate checkIn, LocalDate checkOut, Integer guestCount);

    List<RoomDTOResponse> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, Integer guestCount);

    List<BookingDTOResponse> getCheckInsOfToday();

    List<BookingDTOResponse> getBookingsToConfirmInDays(Integer days);

    void processNoShowBookings();

    boolean existsByRoomId(Long roomId);
}
