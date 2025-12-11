package com.application.smartrideshare.config;


import com.application.smartrideshare.model.Passenger;
import com.application.smartrideshare.model.Role;
import com.application.smartrideshare.repository.PassengerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
@Slf4j
public class DefaultPassengerConfig {

    @Value("${app.default.passenger.username}")
    private String defaultUsername;

    @Value("${app.default.passenger.password}")
    private String defaultPassword;

    @Bean
    public ApplicationRunner initializeDefaultPassenger(PassengerRepository passengerRepository) {
        log.info("Inside DefaultPassengerConfig - initializeDefaultPassenger");
        return args -> {
            if(passengerRepository.findByUsername(defaultUsername).isEmpty()){
                Passenger passenger=Passenger.builder()
                        .username(defaultUsername)
                        .password(defaultPassword)
                        .role(Role.PASSENGER)// In a real application, ensure this password is hashed
                        .fullname("John Cena")
                        .isFirstLogin(false)
                        .createdAt(Instant.now())
                        .contactNumber("8765423983")
                        .age(25)
                        .build();
                passengerRepository.save(passenger);
                log.info("✅ Default passenger created: " + defaultUsername);
            } else {
                log.info("ℹ️ Default passenger already exists: " + defaultUsername);
            }
        };
    }
}
