package com.application.smartrideshare.config;

import com.application.smartrideshare.model.Admin;
import com.application.smartrideshare.model.Role;
import com.application.smartrideshare.repository.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
@Slf4j
public class DefaultAdminConfig {

    @Value("${app.default.admin.username}")
    private String defaultUsername;

    @Value("${app.default.admin.password}")
    private String defaultPassword;

    @Bean
    public ApplicationRunner initializeDefaultAdmin(AdminRepository adminRepository) {
        log.info("Inside DefaultAdminConfig - initializeDefaultAdmin");
        return args -> {
            if (adminRepository.findByUsername(defaultUsername).isEmpty()) {
                Admin admin = Admin.builder()
                        .username(defaultUsername)
                        .password(defaultPassword)
                        .role(Role.ADMIN)
                        .fullname("System Administrator")
                        .isFirstLogin(false)
                        .age(24)
                        .department("IT")
                        .contactNumber("123-456-7890")
                        .createdAt(Instant.now())
                        .build();
                adminRepository.save(admin);
                log.info("✅ Default admin created: " + defaultUsername);
            } else {
                log.info("ℹ️ Default admin already exists: " + defaultUsername);
            }
        };
    }
}