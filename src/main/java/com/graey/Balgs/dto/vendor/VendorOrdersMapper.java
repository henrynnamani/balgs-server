package com.graey.Balgs.dto.vendor;

import com.graey.Balgs.dto.admin.orders.AdminOrderItemResponse;
import com.graey.Balgs.dto.order.OrderItemAddOnResponse;
import com.graey.Balgs.dto.order.OrderProductSummary;
import com.graey.Balgs.dto.order.ProductResponse;
import com.graey.Balgs.model.Order;
import com.graey.Balgs.model.OrderItem;
import com.graey.Balgs.model.OrderItemAddOn;
import org.springframework.stereotype.Component;

@Component
public class VendorOrdersMapper {
    public VendorOrdersResponse toResponse(Order order) {
        return VendorOrdersResponse.builder()
                .id(order.getId())
                .customer(order.getUser().getUsername())
                .phoneNumber(order.getUser().getPhoneNumber())
                .totalPrice(order.getTotalPrice())
                .item(toOrderItemResponse(order.getItem()))
                .status(order.getStatus().name())
                .address(order.getDeliveryAddress().getState() + "," + order.getDeliveryAddress().getCity() + "," + order.getDeliveryAddress().getStreetAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }

    public AdminOrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return AdminOrderItemResponse.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .purchaseTime(orderItem.getPurchaseDate())
                .vendorName(orderItem.getProduct().getVendor().getBusinessName())
                .vendorId(orderItem.getProduct().getVendor().getId())
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
