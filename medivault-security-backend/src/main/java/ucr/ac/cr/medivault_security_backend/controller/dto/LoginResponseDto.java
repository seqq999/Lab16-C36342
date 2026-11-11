package ucr.ac.cr.medivault_security_backend.controller.dto;

public record LoginResponseDto(
        String token,
        String tokenType,
        long expiresInMs,
        String username,
        String rol
) {}
