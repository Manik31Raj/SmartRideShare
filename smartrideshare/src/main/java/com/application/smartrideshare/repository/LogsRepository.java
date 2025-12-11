package com.application.smartrideshare.repository;

import com.application.smartrideshare.model.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogsRepository extends JpaRepository<Logs, Long> {
    Optional<Logs> findTopByUsernameOrderByTimestampDesc(String username);

    void deleteByUsername(String username);
}