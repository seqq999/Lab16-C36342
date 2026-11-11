package ucr.ac.cr.medivault_security_backend.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.medivault_security_backend.data.AuditLogRepository;
import ucr.ac.cr.medivault_security_backend.domain.AuditLog;

import java.util.List;

/**
 * Reto B: auditoria de seguridad.
 * - @Async: la escritura corre en otro hilo, la peticion del usuario no espera (desacoplado).
 * - REQUIRES_NEW: el registro se guarda aunque la transaccion de negocio haga rollback.
 * - Solo se guardan usuario, IP, accion, codigo y el ID del recurso: nunca datos clinicos.
 */
@Service
public class SecurityAuditorService {

    public static final String IDOR_ATTEMPT_BLOCKED = "IDOR_ATTEMPT_BLOCKED";
    public static final String LOGIN_FAILED = "LOGIN_FAILED";

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditorService.class);

    private final AuditLogRepository auditRepo;

    public SecurityAuditorService(AuditLogRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarIntentoIdor(String username, String ip, String recurso) {
        guardar(new AuditLog(username, ip, IDOR_ATTEMPT_BLOCKED, 403, recurso));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarLoginFallido(String username, String ip) {
        guardar(new AuditLog(username, ip, LOGIN_FAILED, 401, "/api/v1/auth/login"));
    }

    @Transactional(readOnly = true)
    public List<AuditLog> listarEventos() {
        return auditRepo.findAllByOrderByFechaEventoDesc();
    }

    private void guardar(AuditLog evento) {
        try {
            auditRepo.save(evento);
        } catch (RuntimeException e) {
            // Una falla de auditoria nunca debe tumbar la peticion del usuario
            log.error("No se pudo registrar evento de auditoria {}", evento.getAccion(), e);
        }
    }
}
