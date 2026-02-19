package com.graey.Balgs.common.mapper;

import com.graey.Balgs.dto.order.OrderItemAddOnResponse;
import com.graey.Balgs.dto.order.OrderItemResponse;
import com.graey.Balgs.dto.order.OrderResponse;
import com.graey.Balgs.model.Order;
import com.graey.Balgs.model.OrderItem;
import com.graey.Balgs.model.OrderItemAddOn;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderMapper::toItemResponse)
                .collect(Collectors.toList());

        System.out.println(items);

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getTotalPrice(),
                items,
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        List<OrderItemAddOnResponse> addons = item.getAddons().stream()
                .map(OrderMapper::toAddOnResponse)
                .collect(Collectors.toList());

        return new OrderItemResponse(
                item.getId(),
                item.getProduct(),
                item.getOrder().getId(),
                item.getPriceAtPurchase(),
                addons,
                item.getPurchaseDate()
        );
    }

    private static OrderItemAddOnResponse toAddOnResponse(OrderItemAddOn addOn) {
        return new OrderItemAddOnResponse(
                addOn.getId(),
                addOn.getOrderItem().getId(),
                addOn.getProduct(),
                addOn.getPriceAtPurchase()
        );
    }
}
