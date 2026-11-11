package ucr.ac.cr.medivault_security_backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ucr.ac.cr.medivault_security_backend.business.SecurityAuditorService;
import ucr.ac.cr.medivault_security_backend.security.ClientIpResolver;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalSecurityExceptionHandler {

    private final SecurityAuditorService auditor;

    public GlobalSecurityExceptionHandler(SecurityAuditorService auditor) {
        this.auditor = auditor;
    }

    @ExceptionHandler(AccessDeniedSecurityException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedSecurityException ex,
                                            HttpServletRequest request) {
        // Reto B: intercepta el evento IDOR y lo registra en AUDIT_LOGS
        auditor.registrarIntentoIdor(usuarioActual(), ClientIpResolver.resolve(request),
                recursoSolicitado(request));

        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        p.setTitle("Acceso Prohibido (IDOR / BOLA)");
        p.setType(URI.create("https://medivault.ucr.ac.cr/forbidden"));
        return p;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        p.setTitle("Recurso No Encontrado");
        p.setType(URI.create("https://medivault.ucr.ac.cr/not-found"));
        return p;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Credenciales invalidas.");
        p.setTitle("Fallo de Autenticacion");
        p.setType(URI.create("https://medivault.ucr.ac.cr/unauthorized"));
        return p;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validacion de datos fallida.");
        p.setTitle("Error de Validacion");
        p.setType(URI.create("https://medivault.ucr.ac.cr/bad-request"));

        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        p.setProperty("invalid_fields", errors);
        return p;
    }

    // JSON mal formado, tipos incorrectos en la URL (/expedientes/abc) o reglas de negocio:
    // 400 sin revelar detalles del parser ni clases internas
    @ExceptionHandler({HttpMessageNotReadableException.class,
                       MethodArgumentTypeMismatchException.class,
                       IllegalArgumentException.class})
    public ProblemDetail handleBadRequest(Exception ex) {
        String detail = ex instanceof IllegalArgumentException ? ex.getMessage() : "Solicitud mal formada.";
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        p.setTitle("Solicitud Invalida");
        p.setType(URI.create("https://medivault.ucr.ac.cr/bad-request"));
        return p;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoRoute(NoResourceFoundException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Ruta no encontrada.");
        p.setTitle("Recurso No Encontrado");
        p.setType(URI.create("https://medivault.ucr.ac.cr/not-found"));
        return p;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethod(HttpRequestMethodNotSupportedException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, "Metodo HTTP no permitido.");
        p.setTitle("Metodo No Permitido");
        p.setType(URI.create("https://medivault.ucr.ac.cr/method-not-allowed"));
        return p;
    }

    // Lanzada por @PreAuthorize (p. ej. un no-admin consultando la auditoria)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleSpringAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Acceso denegado.");
        p.setTitle("Acceso Prohibido");
        p.setType(URI.create("https://medivault.ucr.ac.cr/forbidden"));
        return p;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.");
        p.setTitle("Error Interno");
        p.setType(URI.create("https://medivault.ucr.ac.cr/internal"));
        return p;
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonimo";
    }

    @SuppressWarnings("unchecked")
    private String recursoSolicitado(HttpServletRequest request) {
        Map<String, String> vars = (Map<String, String>)
                request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (vars != null && vars.containsKey("id")) {
            return "EXPEDIENTE:" + vars.get("id");
        }
        return request.getMethod() + " " + request.getRequestURI();
    }
}
