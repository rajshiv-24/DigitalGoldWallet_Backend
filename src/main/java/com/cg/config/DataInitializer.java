package com.cg.config;

import com.cg.entity.Address;
import com.cg.entity.User;
import com.cg.enums.Role;
import com.cg.repo.AddressRepository;
import com.cg.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Runs once on startup.
 * Creates the default ADMIN account if it does not already exist.
 *
 * Default credentials:
 *   Email   : admin@digitalgoldwallet.com
 *   Password: Admin@123
 *
 * IMPORTANT — change the password immediately after first login via
 *   PUT /api/users/{adminUserId}  (with Authorization: Bearer <token>)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final String ADMIN_EMAIL    = "admin@digitalgoldwallet.com";
    private static final String ADMIN_PASSWORD = "Admin@123";
    private static final String ADMIN_NAME     = "System Admin";

    private final UserRepository    userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder   passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AddressRepository addressRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository    = userRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder   = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {
            log.info("Admin account already exists — skipping creation.");
            return;
        }

        // Use address_id = 1 if it exists, otherwise create a placeholder
        Address address = addressRepository.findById(1)
                .orElseGet(() -> {
                    Address a = new Address();
                    a.setStreet("Admin Office");
                    a.setCity("Mumbai");
                    a.setState("Maharashtra");
                    a.setPostalCode("400001");
                    a.setCountry("India");
                    return addressRepository.save(a);
                });

        User admin = new User();
        admin.setName(ADMIN_NAME);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(Role.ADMIN);
        admin.setAddress(address);
        admin.setBalance(BigDecimal.ZERO);
        admin.setCreatedAt(LocalDateTime.now());

        userRepository.save(admin);

        log.info("============================================================");
        log.info("  DEFAULT ADMIN ACCOUNT CREATED");
        log.info("  Email   : {}", ADMIN_EMAIL);
        log.info("  Password: {}", ADMIN_PASSWORD);
        log.info("  !! Change this password after first login !!");
        log.info("============================================================");
    }
}
