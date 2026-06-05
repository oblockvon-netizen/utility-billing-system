package rw.utility.utility_billing_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rw.utility.utility_billing_system.entity.Role;
import rw.utility.utility_billing_system.repository.RoleRepository;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Arrays.stream(rw.utility.utility_billing_system.enums.Role.values())
                .filter(role -> roleRepository.findByName(role).isEmpty())
                .map(role -> Role.builder().name(role).build())
                .forEach(roleRepository::save);
    }
}
