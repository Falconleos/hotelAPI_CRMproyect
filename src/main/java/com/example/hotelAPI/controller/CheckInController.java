package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.CheckInDTORequest;
import com.example.hotelAPI.dto.response.CheckInDTOResponse;
import com.example.hotelAPI.enums.CheckInState;
import com.example.hotelAPI.service.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/private/check-in")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Check-In Controller", description = "Endpoints para la gestión de ingresos, estadías y egresos (Check-Out) del hotel")
public class CheckInController {

    private final CheckInService checkInService;

    // --- 1. Endpoints de Creación y Flujos de Estado ---

    @PostMapping
    @Operation(summary = "Registrar un nuevo Check-In (Ingreso)")
    public ResponseEntity<CheckInDTOResponse> checkIn(@Valid @RequestBody CheckInDTORequest request) {
        CheckInDTOResponse response = checkInService.checkIn(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/checkout")
    @Operation(summary = "Registrar el Check-Out (Egreso) programado")
    public ResponseEntity<CheckInDTOResponse> checkOut(@PathVariable Long id) {
        CheckInDTOResponse response = checkInService.checkOutStay(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/interrupt")
    @Operation(summary = "Interrumpir anticipadamente una estadía activa")
    public ResponseEntity<CheckInDTOResponse> interruptStay(
            @PathVariable Long id,
            @RequestParam String reason) {
        CheckInDTOResponse response = checkInService.interruptStay(id, reason);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pay")
    @Operation(summary = "Registrar el pago/saldado de una estadía")
    public ResponseEntity<CheckInDTOResponse> payStay(@PathVariable Long id) {
        CheckInDTOResponse response = checkInService.payStay(id);
        return ResponseEntity.ok(response);
    }

    // --- 2. Endpoints de Consulta y Filtros ---

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle de un Check-In por ID")
    public ResponseEntity<CheckInDTOResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(checkInService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos los check-ins (opcional filtrar por estado activo)")
    public ResponseEntity<List<CheckInDTOResponse>> list(
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(checkInService.list(active));
    }

    @GetMapping("/state/{state}")
    @Operation(summary = "Listar check-ins por su estado actual (CURRENTLY_ACTIVE, COMPLETED, INTERRUPTED)")
    public ResponseEntity<List<CheckInDTOResponse>> findByState(@PathVariable CheckInState state) {
        return ResponseEntity.ok(checkInService.findByState(state));
    }

    @GetMapping("/search/last-name")
    @Operation(summary = "Buscar check-ins por apellido del huésped")
    public ResponseEntity<List<CheckInDTOResponse>> findByLastName(@RequestParam String lastName) {
        return ResponseEntity.ok(checkInService.checkInByLastName(lastName));
    }

    @GetMapping("/search/dni")
    @Operation(summary = "Buscar check-ins por DNI del huésped")
    public ResponseEntity<List<CheckInDTOResponse>> findByDni(@RequestParam String dni) {
        return ResponseEntity.ok(checkInService.checkInByDni(dni));
    }

    @GetMapping("/history/room/{roomNumber}")
    @Operation(summary = "Historial de estadías de una habitación ordenado por fecha de salida")
    public ResponseEntity<List<CheckInDTOResponse>> historyByRoom(@PathVariable Integer roomNumber) {
        return ResponseEntity.ok(checkInService.historyCheckInsByRoom(roomNumber));
    }

    @GetMapping("/today-checkouts")
    @Operation(summary = "Listar las estadías activas que finalizan el día de hoy")
    public ResponseEntity<List<CheckInDTOResponse>> getCheckOutsOfTheDay() {
        return ResponseEntity.ok(checkInService.checkOutsOfTheDay());
    }

    // --- 3. Endpoints de KPIs y Métricas ---

    @GetMapping("/kpis/occupancy/{startDate}/{endDate}")
    @Operation(summary = "Obtener porcentaje de ocupación en un rango de fechas")
    public ResponseEntity<Double> getOccupancyRate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResponseEntity.ok(checkInService.occupancyRateByDateRange(startDate, endDate));
    }

    @GetMapping("/kpis/active-count")
    @Operation(summary = "Obtener cantidad de estadías actualmente activas")
    public ResponseEntity<Integer> getActiveStaysCount() {
        return ResponseEntity.ok(checkInService.activeStaysCount());
    }

    @GetMapping("/kpis/revenue/today")
    @Operation(summary = "Obtener la recaudación total de los ingresos confirmados hoy")
    public ResponseEntity<Double> getTodaysCheckInRevenue() {
        return ResponseEntity.ok(checkInService.todaysCheckInRevenue());
    }

    @GetMapping("/kpis/revenue/monthly")
    @Operation(summary = "Obtener recaudación mensual agrupada para un año determinado")
    public ResponseEntity<Map<String, Double>> getRevenueByMonthAndYear(@RequestParam Integer year) {
        return ResponseEntity.ok(checkInService.revenueByMonthAndYear(year));
    }

}
