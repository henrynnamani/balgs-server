package com.graey.Balgs.repo;


import com.graey.Balgs.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendorRepo extends JpaRepository<Vendor, UUID> {
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.status = com.graey.Balgs.common.enums.VendorStatus.APPROVED")
    Long countActiveVendors();
}
