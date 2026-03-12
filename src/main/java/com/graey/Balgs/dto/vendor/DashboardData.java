package com.graey.Balgs.dto.vendor;

import com.graey.Balgs.dto.admin.orders.AdminOrderResponse;
import com.graey.Balgs.dto.order.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {
    private String vendorName;
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long deliveredOrders;
    private BigDecimal averageOrderPerTransaction;
    private double percentageChange;
    private double rating;
    private List<AdminOrderResponse> pendingOrders;
}
