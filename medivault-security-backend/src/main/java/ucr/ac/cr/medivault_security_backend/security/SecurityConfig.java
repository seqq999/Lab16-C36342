package ucr.ac.cr.medivault_security_backend.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final RateLimitingFilter rateFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, RateLimitingFilter rateFilter) {
        this.jwtFilter = jwtFilter;
        this.rateFilter = rateFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivacion CSRF (Stateless JWT)
            .csrf(AbstractHttpConfigurer::disable)

            // 2. Sesion sin estado (Stateless)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. Reglas de Autorizacion
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/h2-console/**").denyAll()
                .requestMatchers("/api/v1/expedientes/**").authenticated()
                .anyRequest().authenticated()
            )

            // Sin token valido -> 401 (en lugar del 403 por defecto)
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )

            // 4. Hardening de Cabeceras HTTP
            .headers(h -> h
                // Bloquea Clickjacking (no iframes)
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                // Bloquea MIME-Sniffing: X-Content-Type-Options: nosniff
                // (el PDF usa ::disable, que QUITA la cabecera; withDefaults la activa)
                .contentTypeOptions(Customizer.withDefaults())
                // CSP Nivel 3
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; " +
                                      "frame-ancestors 'none'; " +
                                      "object-src 'none';")
                )
                // HSTS: obliga uso de HTTPS. Por defecto Spring solo la envia sobre HTTPS;
                // AnyRequestMatcher la envia siempre para poder verla en la Prueba 2 (http local)
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                    .requestMatcher(AnyRequestMatcher.INSTANCE)
                )
                // Prohibe cache de datos medicos: Cache-Control: no-cache, no-store...
                // (el PDF usa ::disable, que QUITA estas cabeceras; withDefaults las activa)
                .cacheControl(Customizer.withDefaults())
            )

            // 5. Insercion de Filtros en Pipeline
            .addFilterBefore(rateFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Los filtros son @Component: se evita que Spring Boot los registre por segunda vez
    // como filtros de servlet globales (solo deben correr dentro de la cadena de seguridad)
    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateFilterRegistration(RateLimitingFilter f) {
        FilterRegistrationBean<RateLimitingFilter> reg = new FilterRegistrationBean<>(f);
        reg.setEnabled(false);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter f) {
        FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>(f);
        reg.setEnabled(false);
        return reg;
    }
}
