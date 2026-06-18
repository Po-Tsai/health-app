package com.healthapp.repository;

import com.healthapp.entity.HealthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthLogRepository extends JpaRepository<HealthLog, Long> {

    Optional<HealthLog> findTopByOrderByLogDateDescIdDesc();
}
