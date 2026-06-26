package com.pspsimulator.service;

import com.pspsimulator.model.SimulationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class PayloadGeneratorService {

    public String generate(SimulationLog simulation, String expectedStatus) {
        String type = simulation.getTransactionType();

        if ("DEPOSIT".equalsIgnoreCase(type)) {
            return generateDepositPayload(simulation, expectedStatus);
        } else {
            return generateWithdrawalPayload(simulation, expectedStatus);
        }
    }

    private String generateDepositPayload(SimulationLog simulation, String expectedStatus) {
        String pspStatus = mapToPspStatus(expectedStatus);
        String txid = expectedStatus.equalsIgnoreCase("APPROVED")
                ? "0x" + UUID.randomUUID().toString().replace("-", "")
                : "";

        return String.format("""
                {
                  "type": "Deposit",
                  "order": {
                    "id": "%s",
                    "amount": %s,
                    "currency": "%s",
                    "timestamp": "%s"
                  },
                  "user": {
                    "id": "",
                    "email": ""
                  },
                  "psp": {
                    "transactionId": "%s",
                    "conversionRate": 0,
                    "status": {
                      "code": "%s",
                      "comment": "%s"
                    },
                    "responseLog": {
                      "code": "%s",
                      "comment": "%s"
                    }
                  }
                }
                """,
                simulation.getOrderId(),
                simulation.getAmount(),
                simulation.getCurrency(),
                Instant.now().toEpochMilli(),
                txid,
                expectedStatus.toLowerCase(),
                pspStatus,
                expectedStatus.toLowerCase(),
                pspStatus
        );
    }

    private String generateWithdrawalPayload(SimulationLog simulation, String expectedStatus) {
        String pspStatus = mapToPspStatus(expectedStatus);
        String txid = expectedStatus.equalsIgnoreCase("APPROVED")
                ? "0x" + UUID.randomUUID().toString().replace("-", "")
                : "";

        return String.format("""
                {
                  "type": "Withdraw",
                  "order": {
                    "id": "%s",
                    "amount": %s,
                    "currency": "%s",
                    "timestamp": "%s"
                  },
                  "user": {
                    "id": "",
                    "email": ""
                  },
                  "psp": {
                    "transactionId": "%s",
                    "conversionRate": 0,
                    "status": {
                      "code": "%s",
                      "comment": "%s"
                    },
                    "responseLog": {
                      "code": "%s",
                      "comment": "%s"
                    }
                  }
                }
                """,
                simulation.getOrderId(),
                simulation.getAmount(),
                simulation.getCurrency(),
                Instant.now().toEpochMilli(),
                txid,
                expectedStatus.toLowerCase(),
                pspStatus,
                expectedStatus.toLowerCase(),
                pspStatus
        );
    }

    private String mapToPspStatus(String expectedStatus) {
        return switch (expectedStatus.toUpperCase()) {
            case "APPROVED" -> "DONE";
            case "REJECTED" -> "FAILED";
            default -> "PENDING";
        };
    }
}