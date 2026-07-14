package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.response.BookingCancellationDTOResponse;
import com.example.hotelAPI.model.BookingCancellationEntity;

import java.util.List;

public interface BookingCancellationService {
    BookingCancellationEntity findEntityById(Long id);

    BookingCancellationDTOResponse findById(Long id);

    List<BookingCancellationDTOResponse> getCancellationHistory();

    List<BookingCancellationDTOResponse> findByGuestLastName(String lastName);

    BookingCancellationEntity create(BookingCancellationEntity cancellation);

    void purgeCancellationHistory();
}
