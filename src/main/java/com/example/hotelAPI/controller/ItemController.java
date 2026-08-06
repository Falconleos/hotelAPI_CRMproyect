package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.ItemDTORequest;
import com.example.hotelAPI.dto.response.ItemDTOResponse;
import com.example.hotelAPI.service.ItemService;
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
@RequestMapping("/private/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Tag(name = "Item Controller", description = "Endpoints para la gestión de ítems individuales")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Listar todos los ítems")
    public ResponseEntity<List<ItemDTOResponse>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Obtener un ítem por su ID")
    public ResponseEntity<ItemDTOResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Crear un nuevo ítem")
    public ResponseEntity<ItemDTOResponse> createItem(@Valid @RequestBody ItemDTORequest request) {
        ItemDTOResponse response = itemService.createItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Actualizar un ítem existente")
    public ResponseEntity<ItemDTOResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDTORequest request) {
        ItemDTOResponse response = itemService.updateItem(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Eliminar un ítem")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}