package com.graey.Balgs.service;

import com.graey.Balgs.common.mapper.OrderMapper;
import com.graey.Balgs.dto.admin.dashboard.*;
import com.graey.Balgs.dto.admin.orders.AdminOrderResponse;
import com.graey.Balgs.dto.admin.orders.AdminOrdersMapper;
import com.graey.Balgs.dto.order.OrderResponse;
import com.graey.Balgs.repo.OrderRepo;
import com.graey.Balgs.repo.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AdminOrdersMapper adminOrdersMapper;

    @Autowired
    private VendorRepo vendorRepo;

    @Autowired
    private OrderRepo orderRepo;

    public Page<AdminOrderResponse> getAllOrder(Pageable pageable) {
        return orderRepo.findAll(pageable).map(adminOrdersMapper::toResponse);
    }

    public DashboardResponse getDashboardData() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);

        BigDecimal totalRevenue = orderRepo.getTotalRevenueSince(sixMonthsAgo);
        BigDecimal thisMonthRevenue = orderRepo.getTotalRevenueSince(oneMonthAgo);
        BigDecimal prevMonthRevenue = orderRepo.getTotalRevenueSince(twoMonthsAgo).subtract(thisMonthRevenue);

        Long totalOrders = orderRepo.countOrdersSince(sixMonthsAgo);
        Long thisMonthOrders = orderRepo.countOrdersSince(oneMonthAgo);
        Long prevMonthOrders = orderRepo.countOrdersSince(twoMonthsAgo) - thisMonthOrders;

        StatsResponse stats = new StatsResponse(
                totalRevenue, totalOrders,
                vendorRepo.countActiveVendors(), vendorRepo.count(),
                0L, 0L,
                calculateGrowth(thisMonthRevenue, prevMonthRevenue),
                calculateGrowth(BigDecimal.valueOf(thisMonthOrders), BigDecimal.valueOf(prevMonthOrders))
        );

        return new DashboardResponse(
                stats,
                mapMonthlyRevenue(sixMonthsAgo),
                mapTopSellingModels(),
                mapOrderStatusBreakdown()
        );
    }

    private List<MonthlyRevenueResponse> mapMonthlyRevenue(LocalDateTime since) {
        return orderRepo.getMonthlyRevenueRaw(since).stream()
                .map(row -> new MonthlyRevenueResponse(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .collect(Collectors.toList());
    }

    private List<TopSellingModelResponse> mapTopSellingModels() {
        return orderRepo.getTopSellingModelsRaw().stream()
                .map(row -> new TopSellingModelResponse(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    private List<OrderStatusBreakdownResponse> mapOrderStatusBreakdown() {
        return orderRepo.getOrderStatusBreakdownRaw().stream()
                .map(row -> new OrderStatusBreakdownResponse(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    private double calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
