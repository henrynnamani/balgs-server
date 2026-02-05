package com.graey.Balgs.repo;


import com.graey.Balgs.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VendorRepo extends JpaRepository<Vendor, UUID> {
}
