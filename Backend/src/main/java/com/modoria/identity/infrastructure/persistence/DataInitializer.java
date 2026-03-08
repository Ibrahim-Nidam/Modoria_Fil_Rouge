package com.modoria.identity.infrastructure.persistence;

import com.modoria.identity.domain.model.Role;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.RoleRepository;
import com.modoria.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initRolesAndAdminUser(RoleRepository roleRepository,
            UserRepository userRepository) {
        return args -> {
            Role adminRole = createRoleIfNotExists(roleRepository, "ADMIN");
            createRoleIfNotExists(roleRepository, "CUSTOMER");
            createRoleIfNotExists(roleRepository, "SUPPORT");

            if (userRepository.findByEmail("a@a.c").isEmpty()) {
                User admin = User.builder()
                        .fullName("System Administrator")
                        .email("a@a.c")
                        .password(passwordEncoder.encode("admin"))
                        .enabled(true)
                        .roles(Set.of(adminRole))
                        .build();

                userRepository.save(admin);
                log.info("Default admin user created: admin@modoria.com / admin123");
            } else {
                log.info("Admin user already exists.");
            }

            log.info("Roles initialization completed: ADMIN, CUSTOMER, SUPPORT");
        };
    }

    private Role createRoleIfNotExists(RoleRepository roleRepository, String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    Role saved = roleRepository.save(role);
                    log.info("Created role: {}", roleName);
                    return saved;
                });
    }
}
