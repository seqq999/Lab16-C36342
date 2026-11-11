package ucr.ac.cr.medivault_security_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.medivault_security_backend.business.AuthService;
import ucr.ac.cr.medivault_security_backend.controller.dto.*;
import ucr.ac.cr.medivault_security_backend.security.ClientIpResolver;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto req,
                                                  HttpServletRequest httpRequest) {
        return ResponseEntity.ok(
                authService.autenticar(req, ClientIpResolver.resolve(httpRequest))
        );
    }
}
