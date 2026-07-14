package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.ChangePasswordDtoRequest;
import com.example.hotelAPI.service.serviceImpl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private/user")
@RequiredArgsConstructor
@Tag(name = "GestionUsuarios", description = "Endpoints privados para la gestión de funcionalidades de usuarios")

public class UserController {

    private final AuthService authService;

    @PostMapping("/change-password")
    @Operation(
            summary = "Cambiar contraseña interna",
            description = "Permite a un usuario autenticado modificar su contraseña actual validando primero su clave vigente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "La contraseña actual provista es incorrecta"),
            @ApiResponse(responseCode = "401", description = "No autorizado / No se encontró usuario en el contexto de seguridad")
    })
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDtoRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }

}
