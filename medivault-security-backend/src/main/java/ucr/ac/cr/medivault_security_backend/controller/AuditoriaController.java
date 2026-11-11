package ucr.ac.cr.medivault_security_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.medivault_security_backend.business.SecurityAuditorService;
import ucr.ac.cr.medivault_security_backend.domain.AuditLog;

import java.util.List;

/**
 * Reto B: consulta de la bitacora AUDIT_LOGS, solo para administradores.
 * Expone unicamente metadatos de seguridad (usuario, IP, accion, codigo, fecha), sin datos clinicos.
 */
@RestController
@RequestMapping("/api/v1/auditoria")
public class AuditoriaController {

    private final SecurityAuditorService auditor;

    public AuditoriaController(SecurityAuditorService auditor) {
        this.auditor = auditor;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AuditLog>> listar() {
        return ResponseEntity.ok(auditor.listarEventos());
    }
}
