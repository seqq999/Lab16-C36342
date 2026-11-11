package ucr.ac.cr.medivault_security_backend.exception;

public class AccessDeniedSecurityException extends RuntimeException {

    public AccessDeniedSecurityException(String msg) {
        super(msg);
    }
}
