package com.example.OperationSystem.config;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.repository.UserRepository;

/**
 * DataInitializer — runs once every time the application starts.
 *
 * PURPOSE:
 * ────────
 * The system has a chicken-and-egg problem on first boot:
 *   - Creating users requires MANAGER role
 *   - But there are no users yet → no one can log in
 *
 * This class solves it by seeding one default MANAGER account
 * if the users table is completely empty.
 *
 * After the app starts, log in as the default admin,
 * create your real users from the UI, then you can change
 * the admin password or delete this account.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // No users exist — seed the default admin account
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@freightflow.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.MANAGER)
                    .department("Management")
                    .build();

            userRepository.save(admin);

            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("  FreightFlow — Default admin account created           ");
            log.info("  Email   : admin@freightflow.com                       ");
            log.info("  Password: admin123                                     ");
            log.info("  Role    : MANAGER (full access)                       ");
            log.info("  → Log in, create your real users, then change this.  ");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } else {
            log.info("FreightFlow started — {} user(s) found in database.", userRepository.count());
        }
    }
}
