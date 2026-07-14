package com.example.hotelAPI.repository;

import com.example.hotelAPI.model.BookingCancellationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingCancellationRepository extends JpaRepository<BookingCancellationEntity,Long> {

}
