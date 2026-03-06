package com.graey.Balgs.dto.order;

import com.graey.Balgs.model.DeliveryAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceOrderRequest {
    @NotNull(message = "Delivery address is required")
    @Valid
    private DeliveryAddress deliveryAddress;
}