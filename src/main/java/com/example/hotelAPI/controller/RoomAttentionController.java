package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.RoomAttentionDTORequest;
import com.example.hotelAPI.dto.response.RoomAttentionDTOResponse;
import com.example.hotelAPI.service.RoomAttentionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private/room-attentions")
@RequiredArgsConstructor
public class RoomAttentionController {

    private final RoomAttentionService roomAttentionService;

    // Tanto Administradores como Recepcionistas pueden ver los consumos de un check-in
    @GetMapping("/check-in/{checkInId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'RECEPCIONIST', 'ROLE_RECEPCIONIST')")
    public ResponseEntity<List<RoomAttentionDTOResponse>> getByCheckIn(@PathVariable Long checkInId) {
        return ResponseEntity.ok(roomAttentionService.getAttentionsByCheckIn(checkInId));
    }

    // Tanto Administradores como Recepcionistas pueden registrar un consumo o servicio
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'RECEPCIONIST', 'ROLE_RECEPCIONIST')")
    public ResponseEntity<RoomAttentionDTOResponse> addAttention(@Valid @RequestBody RoomAttentionDTORequest request) {
        RoomAttentionDTOResponse response = roomAttentionService.addAttention(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Opcional: Si solo el Administrador puede eliminar cargos cargados por error, o si ambos pueden.
    // Aquí lo dejamos abierto para ambos, o puedes ajustarlo solo a ADMIN si lo prefieres.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'RECEPCIONIST', 'ROLE_RECEPCIONIST')")
    public ResponseEntity<Void> removeAttention(@PathVariable Long id) {
        roomAttentionService.removeAttention(id);
        return ResponseEntity.noContent().build();
    }
}