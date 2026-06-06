package rw.utility.utility_billing_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * Spring Boot entry point for the WASAC utility billing API.
 */
public class UtilityBillingSystemApplication {

	public static void main(String[] args) {
		// Bootstraps the Spring context and embedded web server
		SpringApplication.run(UtilityBillingSystemApplication.class, args);
	}

}
