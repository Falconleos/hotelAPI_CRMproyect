package com.example.hotelAPI.repository;

import com.example.hotelAPI.enums.CheckInState;
import com.example.hotelAPI.model.CheckInEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckInEntity,Long> {

    // Buscar el check-in asociado a una reserva específica
    Optional<CheckInEntity> findByBookingEntityId(Long bookingId);

    // Buscar todos los check-ins que pertenezcan a un usuario (huésped) específico
    List<CheckInEntity> findByUserEntityId(Long userId);

    // Buscar check-ins administrados por un empleado específico
    List<CheckInEntity> findByEmployeeEntityId(Long employeeId);

    // Buscar check-ins filtrando por su estado (CURRENT, COMPLETED, INTERRUPTED)
    List<CheckInEntity> findByCheckInState(CheckInState checkInState);

    // Buscar todos los check-ins activos (active = true)
    List<CheckInEntity> findByActiveTrue();

    // Buscar check-ins que estén pendientes de pago (paid = false)
    List<CheckInEntity> findByPaidFalse();

}
