package com.graey.Balgs.repo;

import com.graey.Balgs.model.RatingSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingSummaryRepo extends JpaRepository<RatingSummary, UUID> {
    Optional<RatingSummary> findByVendorId(UUID vendorId);
}
