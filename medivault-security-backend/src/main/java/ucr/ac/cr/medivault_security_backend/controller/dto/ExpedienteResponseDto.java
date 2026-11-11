package ucr.ac.cr.medivault_security_backend.controller.dto;

import java.time.LocalDateTime;

public record ExpedienteResponseDto(
        Long id,
        String codigoExpediente,
        String pacienteNombre,
        String medicoNombre,
        String diagnosticoPrincipal,
        String notasClinicas,
        String nivelConfidencialidad,
        LocalDateTime fechaRegistro
) {}
