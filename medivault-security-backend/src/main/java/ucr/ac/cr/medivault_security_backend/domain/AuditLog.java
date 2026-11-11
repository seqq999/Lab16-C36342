package ucr.ac.cr.medivault_security_backend.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "AUDIT_LOGS")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, updatable = false)
    private String username;

    @Column(name = "ip_origen", nullable = false, length = 45, updatable = false)
    private String ipOrigen;

    @Column(nullable = false, length = 50, updatable = false)
    private String accion;

    @Column(name = "codigo_estado", nullable = false, updatable = false)
    private int codigoHttp;

    @Column(name = "recurso_solicitado", nullable = false, length = 255, updatable = false)
    private String recurso;

    @Column(name = "detalle_evento", length = 255, updatable = false)
    private String detalleEvento;

    @Column(name = "fecha_evento", updatable = false)
    private LocalDateTime fechaEvento;

    protected AuditLog() {
    }

    public AuditLog(String username, String ipOrigen, String accion,
            int codigoHttp, String recurso) {
        this(username, ipOrigen, accion, codigoHttp, recurso, null);
    }

    public AuditLog(String username, String ipOrigen, String accion,
            int codigoHttp, String recurso, String detalleEvento) {
        this.username = username;
        this.ipOrigen = ipOrigen;
        this.accion = accion;
        this.codigoHttp = codigoHttp;
        this.recurso = recurso;
        this.detalleEvento = detalleEvento;
        this.fechaEvento = LocalDateTime.now();
    }

    public String getDetalleEvento() {
        return detalleEvento;
    }

    // Solo getters: no existen setters para preservar la inmutabilidad
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public String getAccion() {
        return accion;
    }

    public int getCodigoHttp() {
        return codigoHttp;
    }

    public String getRecurso() {
        return recurso;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }
}
