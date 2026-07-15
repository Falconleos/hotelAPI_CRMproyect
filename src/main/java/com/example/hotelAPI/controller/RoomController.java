package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.RoomDTORequest;
import com.example.hotelAPI.dto.request.RoomUpdateDTO;
import com.example.hotelAPI.dto.response.RoomDTOResponse;
import com.example.hotelAPI.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private/room")
@RequiredArgsConstructor
@Tag(name = "GestionHabitaciones", description = "Endpoints privados para administrar las habitaciones físicas del hotel")
public class RoomController {

    private final RoomService roomService;

    /*--------- Crear una nueva Habitación (ADMIN) ---------------*/
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva habitación", description = "Registra una habitación física en el hotel asignándole un tipo preexistente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Habitación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "El número de habitación ya existe o el tipo de habitación es inválido"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<RoomDTOResponse> createRoom(@Valid @RequestBody RoomDTORequest request) {
        RoomDTOResponse savedRoom = roomService.save(request);
        return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);
    }

    /*--------- Obtener habitación por ID (ADMIN y RECEPCIONIST) ---------------*/
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Obtener habitación por ID")
    public ResponseEntity<RoomDTOResponse> getById(@PathVariable Long id) {
        RoomDTOResponse room = roomService.findById(id);
        return ResponseEntity.ok(room);
    }

    /*--------- Listar todas las habitaciones (ADMIN y RECEPCIONIST) ---------------*/
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Listar todas las habitaciones físicas registradas")
    public ResponseEntity<List<RoomDTOResponse>> getAll() {
        List<RoomDTOResponse> rooms = roomService.getAll();
        return ResponseEntity.ok(rooms);
    }

    /*--------- Listar solo habitaciones disponibles (ADMIN y RECEPCIONIST) ---------------*/
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Listar habitaciones que están actualmente disponibles")
    public ResponseEntity<List<RoomDTOResponse>> getAvailableRooms() {
        List<RoomDTOResponse> available = roomService.availableRooms();
        return ResponseEntity.ok(available);
    }

    /*--------- Actualizar habitación (ADMIN) ---------------*/
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar número o tipo de una habitación")
    public ResponseEntity<RoomDTOResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomUpdateDTO request
    ) {
        RoomDTOResponse updatedRoom = roomService.update(id, request);
        return ResponseEntity.ok(updatedRoom);
    }

    /*--------- Poner habitación en Mantenimiento (ADMIN o RECEPCIONIST) ---------------*/
    @PatchMapping("/{id}/maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Cambiar el estado de la habitación a mantenimiento")
    public ResponseEntity<RoomDTOResponse> putInMaintenance(@PathVariable Long id) {
        RoomDTOResponse maintainedRoom = roomService.doMaintenance(id);
        return ResponseEntity.ok(maintainedRoom);
    }

    /*--------- Eliminar habitación (ADMIN) ---------------*/
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar físicamente una habitación", description = "No se puede eliminar si su estado es OCCUPIED.")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /*--------- Obtener cantidad total de habitaciones (ADMIN y RECEPCIONIST) ---------------*/
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Obtener el conteo total de habitaciones registradas")
    public ResponseEntity<Integer> getRoomCount() {
        return ResponseEntity.ok(roomService.roomCount());
    }

}
