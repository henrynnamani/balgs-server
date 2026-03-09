package com.graey.Balgs.dto.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopSellingModelResponse {
    private String model;
    private Long totalSold;
}