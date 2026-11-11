package ucr.ac.cr.medivault_security_backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.medivault_security_backend.business.ExpedienteService;
import ucr.ac.cr.medivault_security_backend.controller.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expedientes")
public class ExpedienteController {

    private final ExpedienteService expService;

    public ExpedienteController(ExpedienteService expService) {
        this.expService = expService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpedienteResponseDto> obtenerPorId(@PathVariable Long id, Authentication auth) {
        String user = auth.getName();
        return ResponseEntity.ok(expService.consultarPorId(id, user));
    }

    @GetMapping("/mis-expedientes")
    public ResponseEntity<List<ExpedienteResponseDto>> obtenerMisExpedientes(Authentication auth) {
        String user = auth.getName();
        return ResponseEntity.ok(expService.listarExpedientesPropios(user));
    }

    @PostMapping
    public ResponseEntity<ExpedienteResponseDto> crearExpediente(@Valid @RequestBody ExpedienteRequestDto req,
                                                                 Authentication auth) {
        String doc = auth.getName();
        ExpedienteResponseDto creado = expService.registrarExpediente(req, doc);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Parte 2: reemplazo seguro de actualizarNotasVulnerable
    // (recibe Authentication, valida con DTO y delega la verificacion de propiedad al servicio)
    @PatchMapping("/{id}/notas")
    public ResponseEntity<Void> actualizarNotas(@PathVariable Long id,
                                                @Valid @RequestBody ActualizarNotasRequestDto req,
                                                Authentication auth) {
        expService.actualizarNotasDefensivo(id, req.notasClinicas(), auth.getName());
        return ResponseEntity.noContent().build();
    }
}
