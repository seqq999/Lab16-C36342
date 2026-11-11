package ucr.ac.cr.medivault_security_backend.data;

import org.springframework.data.repository.Repository;
import ucr.ac.cr.medivault_security_backend.domain.AuditLog;
import java.util.List;

/**
 * Reto B: repositorio "append-only". Extiende Repository (no JpaRepository)
 * para exponer solo save y lecturas; no hay delete ni update.
 */
public interface AuditLogRepository extends Repository<AuditLog, Long> {

    AuditLog save(AuditLog log);

    List<AuditLog> findAllByOrderByFechaEventoDesc();
}
