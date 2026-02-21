package com.graey.Balgs.common.mapper;


import com.graey.Balgs.dto.order.OrderItemAddOnResponse;
import com.graey.Balgs.dto.order.OrderItemResponse;
import com.graey.Balgs.dto.order.OrderResponse;
import com.graey.Balgs.dto.order.ProductResponse;
import com.graey.Balgs.model.Order;
import com.graey.Balgs.model.OrderItem;
import com.graey.Balgs.model.OrderItemAddOn;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse().builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .totalPrice(order.getTotalPrice())
                .items(
                        order.getItems().stream()
                                .map(this::toOrderItemResponse)
                                .toList()
                )
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();

        return response;
    }

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse().builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .purchaseTime(orderItem.getPurchaseDate())
                .product(
                        ProductResponse.builder()
                                .id(orderItem.getProduct().getId())
                                .name(orderItem.getProduct().getModel())
                                .price(orderItem.getProduct().getPrice())
                                .build()
                )
                .addons(
                        orderItem.getAddons().stream()
                                .map(this::toOrderItemAddOnResponse)
                                .toList()
                )
                .build();

        return response;
    }

    public OrderItemAddOnResponse toOrderItemAddOnResponse(OrderItemAddOn addon) {
        OrderItemAddOnResponse response = new OrderItemAddOnResponse().builder()
                .id(addon.getId())
                .orderItemId(addon.getOrderItem().getId())
                .product(
                        ProductResponse.builder()
                                .id(addon.getProduct().getId())
                                .name(addon.getProduct().getName())
                                .price(addon.getProduct().getPrice())
                                .build()
                )
                .priceAtPurchase(addon.getPriceAtPurchase())
                .build();

        return response;
    }
}
