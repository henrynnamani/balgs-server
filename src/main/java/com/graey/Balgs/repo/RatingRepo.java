package com.graey.Balgs.repo;

import com.graey.Balgs.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepo extends JpaRepository<Rating, UUID> {
    Optional<Rating> findByVendor_IdAndUser_Id(UUID userId, UUID vendorId);

    @Query("""
            SELECT r.rating, COUNT(r)
            FROM Rating r
            WHERE r.vendor.id = :vendorId
            GROUP BY r.rating
            """)
    List<Object[]> getRatingDistribution(UUID vendorId);
}
