package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.CommentDTORequest;
import com.example.hotelAPI.dto.response.CommentDTOResponse;
import com.example.hotelAPI.service.CommentService;
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

import com.example.hotelAPI.model.UserEntity;
import com.example.hotelAPI.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/private/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Tag(name = "GestionComentarios", description = "Endpoints para la gestión de valoraciones y comentarios de estadías")
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('GUEST', 'ADMIN')")
    @Operation(summary = "Crear un comentario", description = "Permite a un huésped comentar y valorar (1-5) una estadía de la cual formó parte.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comentario creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o el usuario no formó parte de la estadía")
    })
    public ResponseEntity<CommentDTOResponse> createComment(
            @Valid @RequestBody CommentDTORequest request) {
        CommentDTOResponse response = commentService.createComment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/check-in/{checkInId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST', 'GUEST')")
    @Operation(summary = "Listar comentarios por estadía")
    public ResponseEntity<List<CommentDTOResponse>> getCommentsByCheckIn(@PathVariable Long checkInId) {
        return ResponseEntity.ok(commentService.getCommentsByCheckIn(checkInId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST', 'GUEST')")
    @Operation(summary = "Listar comentarios (Todos para Admin/Recepcionista, propios para Guest)")
    public ResponseEntity<List<CommentDTOResponse>> getAllComments() {
        // Obtenemos el usuario autenticado para verificar su rol
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdminOrReceptionist = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equalsIgnoreCase("ADMIN") ||
                        role.getName().name().equalsIgnoreCase("RECEPCIONIST"));

        if (isAdminOrReceptionist) {
            return ResponseEntity.ok(commentService.getAllComments());
        } else {
            return ResponseEntity.ok(commentService.getMyComments());
        }
    }

    @GetMapping("/my-comments")
    @PreAuthorize("hasRole('GUEST')")
    @Operation(summary = "Listar los comentarios propios del huésped logueado")
    public ResponseEntity<List<CommentDTOResponse>> getMyComments() {
        return ResponseEntity.ok(commentService.getMyComments());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Eliminar un comentario")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GUEST', 'ADMIN')")
    @Operation(summary = "Actualizar un comentario", description = "Permite modificar el contenido o la valoración de un comentario existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentario actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Comentario no encontrado")
    })
    public ResponseEntity<CommentDTOResponse> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentDTORequest request) {
        CommentDTOResponse response = commentService.updateComment(id, request);
        return ResponseEntity.ok(response);
    }
}