package com.example.hotelAPI.dto.response;

import com.example.hotelAPI.model.RoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

@Schema(description = "Datos detallados de perfil de usuario devueltos por la API")
public class UserDtoResponse {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario en el sistema", example = "1")
    private Long id;

    @Column(unique = true)
    @Schema(description = "Nombre de usuario único", example = "johndoe99")
    private String username;

    @ManyToMany(fetch = FetchType.EAGER)
    @Schema(description = "Conjunto de roles asignados al usuario")
    private Set<RoleEntity> roles;

    @Column(nullable = false, length = 50)
    @Schema(description = "Nombre(s) del usuario", example = "John")
    private String name;

    @Column(nullable = false, length = 50)
    @Schema(description = "Apellido(s) del usuario", example = "Doe")
    private String surname;

    @Column(nullable = false, unique = true, updatable = false, length = 15)
    @Schema(description = "Documento Nacional de Identidad (DNI)", example = "45678912")
    private String dni;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "Correo electrónico registrado", example = "john.doe@example.com")
    private String email;

    @Column(length = 15, unique = true)
    @Schema(description = "Número de teléfono de contacto", example = "1122334455")
    private String phoneNumber;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Fecha de nacimiento del usuario", example = "1995-05-15")
    private LocalDate birthDay;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Fecha de creación del usuario en el sistema", example = "2026-03-10")
    private LocalDate createAt;

    @Column(nullable = false)
    @Schema(description = "Indica si la cuenta del usuario no ha expirado", example = "true")
    private boolean accountNonExpired;

    @Column(nullable = false)
    @Schema(description = "Indica si el usuario se encuentra desbloqueado", example = "true")
    private boolean accountNonLocked;

    @Column(nullable = false)
    @Schema(description = "Indica si las credenciales (contraseña) no han expirado", example = "true")
    private boolean credentialsNonExpired;

    @Column(nullable = false)
    @Schema(description = "Indica si la cuenta está habilitada y activa para operar", example = "true")
    private boolean enabled;

}
