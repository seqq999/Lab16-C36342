package ucr.ac.cr.medivault_security_backend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/**
 * Reto A: obtiene la IP real del cliente.
 * Si la peticion pasa por un proxy inverso se usa el primer valor de X-Forwarded-For
 * (la IP original); si no existe, se usa request.getRemoteAddr().
 * Nota: X-Forwarded-For solo es confiable si el proxy lo sobrescribe; en produccion
 * debe aceptarse unicamente cuando viene de proxies conocidos.
 */
public final class ClientIpResolver {

    private ClientIpResolver() {}

    public static String resolve(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            String primera = xff.split(",")[0].trim();
            if (StringUtils.hasText(primera)) {
                return primera;
            }
        }
        return request.getRemoteAddr();
    }
}
