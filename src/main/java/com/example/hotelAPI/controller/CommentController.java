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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Tag(name = "GestionComentarios", description = "Endpoints para la gestión de valoraciones y comentarios de estadías")
public class CommentController {

    private final CommentService commentService;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Listar todos los comentarios del sistema (Admin/Recepcionista)")
    public ResponseEntity<List<CommentDTOResponse>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Eliminar un comentario")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}