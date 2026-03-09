package com.graey.Balgs.dto.order;

import com.graey.Balgs.common.enums.OrderStatus;

public record UpdateOrderStatusRequest(OrderStatus status) {}