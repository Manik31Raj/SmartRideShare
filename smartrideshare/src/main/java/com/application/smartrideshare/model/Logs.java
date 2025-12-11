package com.application.smartrideshare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "log_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Logs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // ADMIN, DRIVER, PASSENGER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogsStatus statusOfAccount; // LOGIN or LOGOUT

    private Instant timestamp;

    @PrePersist
    public void onPrePersist() {
         timestamp = Instant.now();
    }
}
