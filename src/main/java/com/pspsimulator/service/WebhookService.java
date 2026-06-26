package com.pspsimulator.service;

import com.pspsimulator.model.SimulationLog;
import com.pspsimulator.model.WebhookLog;
import com.pspsimulator.repository.WebhookLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookLogRepository webhookLogRepository;

    public WebhookLog deliver(SimulationLog simulation,
                              String payload,
                              String targetUrl) {
        WebhookLog webhookLog = new WebhookLog();
        webhookLog.setSimulation(simulation);
        webhookLog.setWebhookPayload(payload);

        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build()) {

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = client.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            webhookLog.setResponseStatus(response.statusCode());
            webhookLog.setResponseBody(response.body());
            webhookLog.setDeliveryStatus(
                    response.statusCode() >= 200 && response.statusCode() < 300
                            ? "SUCCESS"
                            : "FAILED"
            );

            log.info("Webhook delivered to {} - status: {}",
                    targetUrl, response.statusCode());

        } catch (Exception e) {
            log.error("Webhook delivery failed to {} - error: {}",
                    targetUrl, e.getMessage());
            webhookLog.setDeliveryStatus("FAILED");
            webhookLog.setResponseBody(e.getMessage());
        }

        return webhookLogRepository.save(webhookLog);
    }
}