package com.graey.Balgs.dto.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusBreakdownResponse {
    private String status;
    private Long count;
}