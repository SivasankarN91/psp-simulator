package com.pspsimulator.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SimulationRequest {

    @NotBlank(message = "PSP name is required")
    private String pspName;

    @NotBlank(message = "Transaction type is required")
    private String transactionType; // DEPOSIT or WITHDRAWAL

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Target URL is required")
    private String targetUrl;

    private String expectedStatus; // APPROVED, REJECTED (optional - defaults to APPROVED)
}