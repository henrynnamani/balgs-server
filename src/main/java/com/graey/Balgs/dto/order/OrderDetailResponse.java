package com.graey.Balgs.dto.order;

import com.graey.Balgs.model.DeliveryAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {
    private UUID id;
    private String status;
    private List<OrderItemResponse> items;
    private BigDecimal totalPrice;
    private String reference;
    private DeliveryAddress deliveryAddress;
}
