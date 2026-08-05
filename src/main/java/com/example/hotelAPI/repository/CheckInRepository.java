package com.example.hotelAPI.repository;

import com.example.hotelAPI.enums.CheckInState;
import com.example.hotelAPI.model.CheckInEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckInEntity,Long> {

    Optional<CheckInEntity> findByBookingEntityId(Long bookingId);

    List<CheckInEntity> findByUserEntityId(Long userId);

    List<CheckInEntity> findByEmployeeEntityId(Long employeeId);

    List<CheckInEntity> findByCheckInState(CheckInState checkInState);

    List<CheckInEntity> findByActiveTrue();

}