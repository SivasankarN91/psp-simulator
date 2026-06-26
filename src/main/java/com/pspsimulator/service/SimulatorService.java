package com.pspsimulator.service;

import com.pspsimulator.model.SimulationLog;
import com.pspsimulator.model.SimulationRequest;
import com.pspsimulator.model.WebhookLog;
import com.pspsimulator.repository.SimulationLogRepository;
import com.pspsimulator.repository.WebhookLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulatorService {

    private final SimulationLogRepository simulationLogRepository;
    private final WebhookLogRepository webhookLogRepository;
    private final PayloadGeneratorService payloadGeneratorService;
    private final WebhookService webhookService;

    public SimulationLog startSimulation(SimulationRequest request) {
        // Generate order ID
        String orderId = "ortx" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();

        // Save simulation log
        SimulationLog simulation = new SimulationLog();
        simulation.setOrderId(orderId);
        simulation.setPspName(request.getPspName());
        simulation.setTransactionType(request.getTransactionType());
        simulation.setAmount(request.getAmount());
        simulation.setCurrency(request.getCurrency());
        simulation.setStatus("PENDING");
        simulation.setTargetUrl(request.getTargetUrl());

        SimulationLog saved = simulationLogRepository.save(simulation);
        log.info("Simulation started - orderId: {}, PSP: {}", orderId, request.getPspName());

        // Run webhook delivery in background
        new Thread(() -> {
            try {
                // Simulate PSP processing delay (3 seconds)
                Thread.sleep(3000);

                // Generate webhook payload
                String expectedStatus = request.getExpectedStatus() != null
                        ? request.getExpectedStatus()
                        : "APPROVED";

                String payload = payloadGeneratorService.generate(
                        saved,
                        expectedStatus
                );

                // Deliver webhook to target URL
                WebhookLog webhookLog = webhookService.deliver(
                        saved,
                        payload,
                        request.getTargetUrl()
                );

                // Update simulation status
                saved.setStatus(expectedStatus);
                simulationLogRepository.save(saved);

                log.info("Simulation completed - orderId: {}, status: {}",
                        orderId, expectedStatus);

            } catch (InterruptedException e) {
                log.error("Simulation interrupted - orderId: {}", orderId);
                Thread.currentThread().interrupt();
            }
        }).start();

        return saved;
    }

    public Optional<SimulationLog> getSimulation(Long id) {
        return simulationLogRepository.findById(id);
    }

    public List<SimulationLog> getAllSimulations() {
        return simulationLogRepository.findAll();
    }

    public List<WebhookLog> getWebhookLogs(Long simulationId) {
        return webhookLogRepository.findBySimulationId(simulationId);
    }
}