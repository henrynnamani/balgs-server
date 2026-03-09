package com.graey.Balgs.dto.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsResponse {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long activeVendors;
    private Long totalVendors;
    private Long openTradeIns;
    private Long totalTradeIns;
    private Double revenueGrowth;
    private Double ordersGrowth;
}
