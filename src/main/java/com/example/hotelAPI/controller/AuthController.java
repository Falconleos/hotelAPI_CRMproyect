package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.ChangePasswordDtoRequest;
import com.example.hotelAPI.dto.request.ResetPasswordDtoRequest;
import com.example.hotelAPI.dto.request.UserDtoRequest;
import com.example.hotelAPI.dto.request.UserLoginDtoRequest;
import com.example.hotelAPI.dto.response.AuthSuccessDtoResponse;
import com.example.hotelAPI.dto.response.AuthTokenResponse;
import com.example.hotelAPI.dto.response.RefreshTokenDtoResponse;
import com.example.hotelAPI.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints públicos para la gestión de accesos, registro y tokens")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea una cuenta en el sistema con el rol base GUEST. Retorna el token de acceso JWT en el cuerpo y setea la cookie HttpOnly con el Refresh Token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = AuthTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o conflicto con datos existentes")
    })
    public ResponseEntity<AuthTokenResponse>register(@Valid @RequestBody UserDtoRequest request){
        AuthSuccessDtoResponse token = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, token.responseCookie().toString())
                   .body(token.authTokenResponse());
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario mediante sus credenciales. Si es exitoso, retorna el token de acceso JWT y viaja la cookie HttpOnly del Refresh Token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                    content = @Content(schema = @Schema(implementation = AuthTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o usuario no encontrado")
    })
    public ResponseEntity<AuthTokenResponse>login(@Valid @RequestBody UserLoginDtoRequest request){
        AuthSuccessDtoResponse token = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE,token.responseCookie().toString())
                .body(token.authTokenResponse());
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refrescar token de acceso",
            description = "Recibe el Refresh Token almacenado en las cookies para generar un nuevo token de acceso JWT válido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente",
                    content = @Content(schema = @Schema(implementation = RefreshTokenDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "No se suministró la cookie del Refresh Token"),
            @ApiResponse(responseCode = "403", description = "Refresh Token expirado o inexistente en la base de datos")
    })
    public ResponseEntity<RefreshTokenDtoResponse>refresh(
            @CookieValue(name = "refreshToken",required = false)String refreshtoken){
        if(refreshtoken==null || refreshtoken.isBlank()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        RefreshTokenDtoResponse tokenResponse = authService.refreshAccessToken(refreshtoken);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesión",
            description = "Invalida el Refresh Token del usuario actual en la base de datos y limpia la cookie del navegador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesión cerrada correctamente")
    })
    public ResponseEntity<Void>logout(){
        ResponseCookie cleanCookie = authService.logout();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,cleanCookie.toString())
                .build();
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Solicitar recuperación de contraseña",
            description = "Recibe el email del usuario para generar un token único y temporal de recuperación de contraseña."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud procesada (por seguridad se retorna OK exista o no el email)")
    })
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Restablecer contraseña usando token",
            description = "Valida el token de recuperación recibido por el usuario y asigna la nueva contraseña provista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido, inexistente o expirado")
    })
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordDtoRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

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
