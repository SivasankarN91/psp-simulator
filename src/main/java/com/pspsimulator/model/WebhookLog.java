package com.pspsimulator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "webhook_log")
public class WebhookLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "simulation_id")
    private SimulationLog simulation;

    @Column(name = "webhook_payload", columnDefinition = "TEXT")
    private String webhookPayload;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "delivery_status")
    private String deliveryStatus; // SUCCESS or FAILED

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @PrePersist
    public void prePersist() {
        this.deliveredAt = LocalDateTime.now();
    }
}