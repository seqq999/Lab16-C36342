package ucr.ac.cr.medivault_security_backend.security;

import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting con algoritmo Token Bucket (Bucket4j).
 * Reto A: en lugar de un cubo global compartido, cada IP de cliente tiene su propio cubo.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Reto A: login -> 5 peticiones por minuto POR IP
    private static final int LOGIN_LIMITE_POR_MINUTO = 5;
    // Expedientes -> 30 peticiones por minuto POR IP
    private static final int API_LIMITE_POR_MINUTO = 30;

    // Un cubo independiente por direccion IP (thread-safe)
    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> apiBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String ip = ClientIpResolver.resolve(request);
        Bucket target = null;

        if (path.startsWith("/api/v1/auth/login")) {
            target = authBuckets.computeIfAbsent(ip, k -> nuevoBucket(LOGIN_LIMITE_POR_MINUTO));
        } else if (path.startsWith("/api/v1/expedientes")) {
            target = apiBuckets.computeIfAbsent(ip, k -> nuevoBucket(API_LIMITE_POR_MINUTO));
        }

        if (target != null) {
            if (target.tryConsume(1)) {
                filterChain.doFilter(request, response);
            } else {
                // Rate Limit superado: HTTP 429
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                response.setHeader("Retry-After", "60");

                String json = """
                        {
                          "type": "%s",
                          "title": "%s",
                          "status": 429,
                          "detail": "%s",
                          "instance": "%s"
                        }
                        """.formatted("https://medivault.cr/rl", "Demasiadas Peticiones",
                                      "Limite superado. Espere 60s.", path);
                response.getWriter().write(json);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private Bucket nuevoBucket(int capacidadPorMinuto) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacidadPorMinuto)
                        .refillIntervally(capacidadPorMinuto, Duration.ofMinutes(1))
                        .build())
                .build();
    }
}
