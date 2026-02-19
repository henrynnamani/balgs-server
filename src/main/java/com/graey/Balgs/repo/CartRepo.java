package com.graey.Balgs.repo;

import com.graey.Balgs.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepo extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserId(UUID userId);
    List<Cart> findByLastUpdatedAtBefore(LocalDateTime time);
}
