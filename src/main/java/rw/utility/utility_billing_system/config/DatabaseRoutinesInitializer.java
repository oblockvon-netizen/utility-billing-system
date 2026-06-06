package rw.utility.utility_billing_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Order(2)
@RequiredArgsConstructor
/**
 * Loads PostgreSQL stored procedures from routines.sql after the schema is ready.
 */
public class DatabaseRoutinesInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // PostgreSQL dollar-quoted blocks use ;; as statement delimiter
        populator.setSeparator(";;");        populator.addScript(new ClassPathResource("db/routines.sql"));
        populator.execute(dataSource);
    }
}
