package com.graey.Balgs.dto.vendor;

import com.graey.Balgs.dto.admin.orders.AdminOrderItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorOrdersResponse {
    private UUID id;
    private String customer;
    private String phoneNumber;
    private String status;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private String address;
    private AdminOrderItemResponse item;
}
