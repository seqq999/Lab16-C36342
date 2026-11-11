package ucr.ac.cr.medivault_security_backend.business;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ucr.ac.cr.medivault_security_backend.controller.dto.LoginRequestDto;
import ucr.ac.cr.medivault_security_backend.controller.dto.LoginResponseDto;
import ucr.ac.cr.medivault_security_backend.data.UsuarioRepository;
import ucr.ac.cr.medivault_security_backend.domain.Usuario;
import ucr.ac.cr.medivault_security_backend.security.JwtTokenProvider;

@Service
public class AuthService {

    private final UsuarioRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtProvider;
    private final SecurityAuditorService auditor;

    public AuthService(UsuarioRepository userRepo, PasswordEncoder encoder,
            JwtTokenProvider jwtProvider, SecurityAuditorService auditor) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
        this.auditor = auditor;
    }

    @Transactional(readOnly = true)
    public LoginResponseDto autenticar(LoginRequestDto request, String ipOrigen) {
        Usuario u = userRepo.findByUsername(request.username())
                .orElseThrow(() -> loginFallido(request.username(), ipOrigen));

        if (!encoder.matches(request.password(), u.getPasswordHash())) {
            throw loginFallido(request.username(), ipOrigen);
        }

        if (!"ACTIVO".equals(u.getEstado())) {
            auditor.registrarLoginFallido(request.username(), ipOrigen);
            throw new BadCredentialsException("Cuenta inactiva o bloqueada.");
        }

        String token = jwtProvider.generateToken(u.getUsername(), u.getRol().name());

        return new LoginResponseDto(token, "Bearer", 3600000, u.getUsername(), u.getRol().name());
    }

    private BadCredentialsException loginFallido(String username, String ipOrigen) {
        auditor.registrarLoginFallido(username, ipOrigen);
        return new BadCredentialsException("Credenciales invalidas.");
    }
}
