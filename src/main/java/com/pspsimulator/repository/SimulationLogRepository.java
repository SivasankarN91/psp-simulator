package com.pspsimulator.repository;

import com.pspsimulator.model.SimulationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SimulationLogRepository extends JpaRepository<SimulationLog, Long> {
    List<SimulationLog> findByPspName(String pspName);
    List<SimulationLog> findByTransactionType(String transactionType);
    List<SimulationLog> findByStatus(String status);
}