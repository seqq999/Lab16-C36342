package ucr.ac.cr.medivault_security_backend.controller.dto;

import jakarta.validation.constraints.*;

/** Parte 2: DTO validado para el PATCH de notas clinicas. */
public record ActualizarNotasRequestDto(
        @NotBlank(message = "Notas clinicas requeridas.")
        @Size(min = 10, max = 4000, message = "Notas: 10 a 4000 caracteres.")
        String notasClinicas
) {}
