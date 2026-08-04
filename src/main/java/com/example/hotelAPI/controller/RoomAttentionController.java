package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.RoomAttentionDTORequest;
import com.example.hotelAPI.dto.response.RoomAttentionDTOResponse;
import com.example.hotelAPI.service.RoomAttentionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private/room-attention")
@RequiredArgsConstructor
@Tag(name = "RoomAttention", description = "Endpoints para la gestión de consumos y servicios a la habitación")
public class RoomAttentionController {

    private final RoomAttentionService roomAttentionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Crear un consumo de Room Service asociado a un Check-In")
    public ResponseEntity<RoomAttentionDTOResponse> createRoomAttention(@Valid @RequestBody RoomAttentionDTORequest request) {
        RoomAttentionDTOResponse response = roomAttentionService.createRoomAttention(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/check-in/{checkInId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST', 'GUEST')")
    @Operation(summary = "Listar los consumos de Room Service de una estadía")
    public ResponseEntity<List<RoomAttentionDTOResponse>> getByCheckIn(@PathVariable Long checkInId) {
        return ResponseEntity.ok(roomAttentionService.getRoomAttentionByCheckIn(checkInId));
    }

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Marcar un consumo de Room Service como pago")
    public ResponseEntity<RoomAttentionDTOResponse> payRoomAttention(@PathVariable Long id) {
        return ResponseEntity.ok(roomAttentionService.payRoomAttention(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Eliminar un consumo de Room Service no pagado")
    public ResponseEntity<Void> deleteRoomAttention(@PathVariable Long id) {
        roomAttentionService.deleteRoomAttention(id);
        return ResponseEntity.noContent().build();
    }

}
