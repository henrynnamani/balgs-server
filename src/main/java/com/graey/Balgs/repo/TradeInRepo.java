package com.graey.Balgs.repo;

import com.graey.Balgs.common.enums.TradeInStatus;
import com.graey.Balgs.model.TradeIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TradeInRepo extends JpaRepository<TradeIn, UUID> {
    List<TradeIn> findByUserId(UUID userId);
    List<TradeIn> findByStatus(TradeInStatus status);
}
