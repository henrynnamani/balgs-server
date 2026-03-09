package com.graey.Balgs.repo;

import com.graey.Balgs.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {
    @Query("""
    SELECT p FROM Product p
    LEFT JOIN FETCH p.vendor v
    LEFT JOIN FETCH v.ratings
    LEFT JOIN FETCH v.ratingSummary
    WHERE p.id = :id
""")
    Optional<Product> findByIdWithVendorDetails(@Param("id") UUID id);
}
