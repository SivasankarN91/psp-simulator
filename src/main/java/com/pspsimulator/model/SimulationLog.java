package com.pspsimulator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "simulation_log")
public class SimulationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "psp_name", nullable = false)
    private String pspName;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // DEPOSIT or WITHDRAWAL

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED

    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}