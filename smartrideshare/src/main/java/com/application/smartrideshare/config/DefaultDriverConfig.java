package com.application.smartrideshare.config;

import com.application.smartrideshare.model.Driver;
import com.application.smartrideshare.model.Role;
import com.application.smartrideshare.repository.DriverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
@Slf4j
public class DefaultDriverConfig {

    @Value("${app.default.driver.username}")
    private String defaultUsername;

    @Value("${app.default.driver.password}")
    private String defaultPassword;

    @Bean
    public ApplicationRunner initializeDefaultDriver(DriverRepository driverRepository) {
        log.info("Inside DefaultDriverConfig - initializeDefaultDriver");
        return args -> {
            if(driverRepository.findByUsername(defaultUsername).isEmpty())
            {
                Driver driver=Driver.builder()
                        .username(defaultUsername)
                        .password(defaultPassword)
                        .role(Role.DRIVER)
                        .isFirstLogin(false)
                        .createdAt(Instant.now())
                        .fullname("C.M Punk")
                        .contactNumber("7539283451")
                        .licenseNumber("DEFAULT123")
                        .vehicleModel("Swift Dzire")
                        .vehiclePlate("DEF-0000")
                        .age(30)
                        .build();
                driverRepository.save(driver);
                log.info("✅ Default driver created with username: "+ defaultUsername);
            }else {
                log.info("ℹ️ Default driver already exists with username: "+ defaultUsername);
            }
        };
    }
}
