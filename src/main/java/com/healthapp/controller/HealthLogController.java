package com.healthapp.controller;

import com.healthapp.entity.HealthLog;
import com.healthapp.service.HealthLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health-logs")
public class HealthLogController {

    private final HealthLogService healthLogService;

    public HealthLogController(HealthLogService healthLogService) {
        this.healthLogService = healthLogService;
    }

    @GetMapping
    public List<HealthLog> getAllHealthLogs() {
        return healthLogService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HealthLog createHealthLog(@Valid @RequestBody HealthLog healthLog) {
        return healthLogService.create(healthLog);
    }

    @PutMapping("/{id}")
    public HealthLog updateHealthLog(@PathVariable Long id,
                                     @Valid @RequestBody HealthLog healthLog) {
        return healthLogService.update(id, healthLog);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHealthLog(@PathVariable Long id) {
        healthLogService.delete(id);
    }

    @GetMapping("/risk")
    public Map<String, String> getLatestRiskLevel() {
        return healthLogService.assessLatestRisk();
    }
}
