package com.graey.Balgs.dto.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private StatsResponse stats;
    private List<MonthlyRevenueResponse> revenueOverview;
    private List<TopSellingModelResponse> topSellingModels;
    private List<OrderStatusBreakdownResponse> orderStatusBreakdown;
}