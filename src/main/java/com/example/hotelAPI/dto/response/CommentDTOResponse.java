package com.example.hotelAPI.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Datos detallados de un comentario")
public class CommentDTOResponse {

    @Schema(description = "ID del comentario", example = "1")
    private Long id;

    @Schema(description = "Contenido del comentario")
    private String content;

    @Schema(description = "ValoraciÃ³n numérica (1-5)")
    private Integer rating;

    @Schema(description = "Fecha de creaciÃ³n")
    private LocalDateTime createdAt;

    @Schema(description = "ID de la estadÃ­a asociada")
    private Long checkInId;

    @Schema(description = "InformaciÃ³n del usuario que comentÃ³")
    private UserDtoResponse user;
}