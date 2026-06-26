package com.pspsimulator.controller;

import com.pspsimulator.model.SimulationLog;
import com.pspsimulator.model.SimulationRequest;
import com.pspsimulator.model.WebhookLog;
import com.pspsimulator.service.SimulatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SimulatorController {

    private final SimulatorService simulatorService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "PSP Simulator"
        ));
    }

    @PostMapping("/simulate")
    public ResponseEntity<SimulationLog> simulate(
            @Valid @RequestBody SimulationRequest request) {
        SimulationLog simulation = simulatorService.startSimulation(request);
        return ResponseEntity.ok(simulation);
    }

    @GetMapping("/simulate/{id}")
    public ResponseEntity<SimulationLog> getSimulation(
            @PathVariable Long id) {
        return simulatorService.getSimulation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/simulations")
    public ResponseEntity<List<SimulationLog>> getAllSimulations() {
        return ResponseEntity.ok(simulatorService.getAllSimulations());
    }

    @GetMapping("/simulate/{id}/webhooks")
    public ResponseEntity<List<WebhookLog>> getWebhookLogs(
            @PathVariable Long id) {
        return ResponseEntity.ok(simulatorService.getWebhookLogs(id));
    }
}