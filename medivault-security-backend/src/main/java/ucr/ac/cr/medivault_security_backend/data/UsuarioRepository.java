package ucr.ac.cr.medivault_security_backend.data;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.medivault_security_backend.domain.Usuario;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}
