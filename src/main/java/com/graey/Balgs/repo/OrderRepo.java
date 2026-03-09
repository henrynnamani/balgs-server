package com.graey.Balgs.repo;

import com.graey.Balgs.dto.admin.dashboard.MonthlyRevenueResponse;
import com.graey.Balgs.dto.admin.dashboard.OrderStatusBreakdownResponse;
import com.graey.Balgs.dto.admin.dashboard.TopSellingModelResponse;
import com.graey.Balgs.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<Order, UUID> {
    Page<Order> findByUserId(UUID userId, Pageable pageable);

        @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.createdAt >= :since")
        BigDecimal getTotalRevenueSince(@Param("since") LocalDateTime since);

        @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :since")
        Long countOrdersSince(@Param("since") LocalDateTime since);

        @Query(value = """
        SELECT 
            TO_CHAR(created_at, 'Mon') as month,
            COALESCE(SUM(total_price), 0) as revenue
        FROM orders
        WHERE created_at >= :since
        GROUP BY TO_CHAR(created_at, 'Mon'), DATE_TRUNC('month', created_at)
        ORDER BY DATE_TRUNC('month', created_at)
    """, nativeQuery = true)
        List<Object[]> getMonthlyRevenueRaw(@Param("since") LocalDateTime since);

        @Query(value = """
        SELECT p.model, COUNT(oi.id) as total_sold
        FROM order_items oi
        JOIN products p ON oi.product_id = p.id
        GROUP BY p.model
        ORDER BY total_sold DESC
        LIMIT 5
    """, nativeQuery = true)
        List<Object[]> getTopSellingModelsRaw();

        @Query(value = """
        SELECT status, COUNT(*) as count
        FROM orders
        GROUP BY status
    """, nativeQuery = true)
        List<Object[]> getOrderStatusBreakdownRaw();
}
