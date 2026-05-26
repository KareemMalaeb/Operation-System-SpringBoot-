package com.example.OperationSystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.enums.Role;
import com.example.OperationSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("Admin")
                    .email("admin@freightflow.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.MANAGER)
                    .build();

            userRepository.save(admin);

            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("  FreightFlow — Default admin account created           ");
            log.info("  Email   : admin@freightflow.com                       ");
            log.info("  Password: admin123                                    ");
            log.info("  Role    : MANAGER (full access)                       ");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } else {
            log.info("FreightFlow started — {} user(s) found in database.", userRepository.count());
        }
    }
}