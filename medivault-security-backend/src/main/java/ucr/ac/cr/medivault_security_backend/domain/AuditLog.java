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

    @Column(nullable = false, length = 50, updatable = false)
    private String username;

    @Column(name = "ip_origen", nullable = false, length = 45, updatable = false)
    private String ipOrigen;

    @Column(nullable = false, length = 40, updatable = false)
    private String accion;

    @Column(name = "codigo_http", nullable = false, updatable = false)
    private int codigoHttp;

    @Column(length = 255, updatable = false)
    private String recurso;

    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private LocalDateTime fechaEvento;

    protected AuditLog() {
    }

    public AuditLog(String username, String ipOrigen, String accion,
            int codigoHttp, String recurso) {
        this.username = username;
        this.ipOrigen = ipOrigen;
        this.accion = accion;
        this.codigoHttp = codigoHttp;
        this.recurso = recurso;
        this.fechaEvento = LocalDateTime.now();
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
