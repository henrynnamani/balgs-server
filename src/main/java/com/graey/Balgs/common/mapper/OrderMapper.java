package com.graey.Balgs.common.mapper;
import com.graey.Balgs.dto.order.*;
import com.graey.Balgs.model.Order;
import com.graey.Balgs.model.OrderItem;
import com.graey.Balgs.model.OrderItemAddOn;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .totalPrice(order.getTotalPrice())
                .item(toOrderItemResponse(order.getItem()))
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .purchaseTime(orderItem.getPurchaseDate())
                .product(
                        OrderProductSummary.builder()
                                .id(orderItem.getProduct().getId())
                                .name(orderItem.getProduct().getModel())
                                .color(orderItem.getProduct().getColor())
                                .romSize(orderItem.getProduct().getRomSize().name())
                                .imageUrl(orderItem.getProduct().getImageUrls().getFirst())
                                .condition(String.valueOf(orderItem.getProduct().getCondition()))
                                .build()
                )
                .addons(
                        orderItem.getAddons().stream()
                                .map(this::toOrderItemAddOnResponse)
                                .toList()
                )
                .build();
    }

    public OrderItemAddOnResponse toOrderItemAddOnResponse(OrderItemAddOn addon) {
        return OrderItemAddOnResponse.builder()
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
    }
}
