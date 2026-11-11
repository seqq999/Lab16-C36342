package ucr.ac.cr.medivault_security_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Reto B: la auditoria se escribe en segundo plano
public class MedivaultSecurityBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedivaultSecurityBackendApplication.class, args);
	}

}
