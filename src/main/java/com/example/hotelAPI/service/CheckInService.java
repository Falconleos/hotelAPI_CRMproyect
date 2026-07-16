package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.CheckInDTORequest;
import com.example.hotelAPI.dto.response.CheckInDTOResponse;
import com.example.hotelAPI.enums.CheckInState;
import com.example.hotelAPI.model.CheckInEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CheckInService {

    // 1. Getters por ID
    CheckInEntity getEntityById(Long id);
    CheckInDTOResponse findById(Long id);

    // 2. Listados y filtros
    List<CheckInDTOResponse> list(Boolean active);
    List<CheckInDTOResponse> findByState(CheckInState state);
    List<CheckInDTOResponse> checkInByLastName(String lastName);
    List<CheckInDTOResponse> checkInByDni(String dni);
    List<CheckInDTOResponse> historyCheckInsByRoom(Integer roomNumber);
    List<CheckInDTOResponse> checkOutsOfTheDay();

    // 7. Flujo principal de Check-In
    CheckInDTOResponse checkIn(CheckInDTORequest request);

    // 8. Flujo de Interrupción
    CheckInDTOResponse interruptStay(Long id, String reason);

    // 9. Flujo de Check-Out
    CheckInDTOResponse checkOutStay(Long id);

    // 10. Pago
    CheckInDTOResponse payStay(Long id);

    // 11. Validaciones cruzadas
    boolean existsByCheckInPorHabitacion(Long roomId);

    // KPIs / Estadísticas
    Double occupancyRateByDateRange(LocalDate startDate, LocalDate endDate);
    Integer activeStaysCount();
    Double todaysCheckInRevenue();
    Map<String, Double> revenueByMonthAndYear(Integer year);

}
